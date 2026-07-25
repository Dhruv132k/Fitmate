package com.fitmate.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end flow against real Postgres + Redis containers:
 * register two users, mutual like, assert a match is created and the swiped
 * user disappears from the feed. Runs on `mvn verify` (requires Docker).
 */
@Tag("it")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FitMateFlowIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("fitmate.seed.enabled", () -> "false");
    }

    @Autowired TestRestTemplate rest;
    @Autowired ObjectMapper mapper;

    private String register(String email, String name) throws Exception {
        String body = """
                {"email":"%s","password":"password123","name":"%s",
                 "workoutGoal":"MUSCLE_GAIN","gymName":"Iron Paradise","city":"Pune"}
                """.formatted(email, name);
        ResponseEntity<String> resp = rest.postForEntity("/api/auth/register", json(body), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return mapper.readTree(resp.getBody()).get("token").asText();
    }

    private long userId(String token) throws Exception {
        JsonNode me = mapper.readTree(
                rest.exchange("/api/profile/me", HttpMethod.GET, auth(token), String.class).getBody());
        return me.get("id").asLong();
    }

    @Test
    void twoUsersMutuallyLikeAndMatch() throws Exception {
        String tokenA = register("a@it.dev", "Aay");
        String tokenB = register("b@it.dev", "Bee");
        long idA = userId(tokenA);
        long idB = userId(tokenB);

        // A sees B in the feed
        JsonNode feedA = mapper.readTree(
                rest.exchange("/api/discovery/feed", HttpMethod.GET, auth(tokenA), String.class).getBody());
        assertThat(feedA.findValuesAsText("id")).contains(String.valueOf(idB));

        // A likes B -> no match yet
        JsonNode swipe1 = mapper.readTree(swipe(tokenA, idB));
        assertThat(swipe1.get("matched").asBoolean()).isFalse();

        // B likes A -> match!
        JsonNode swipe2 = mapper.readTree(swipe(tokenB, idA));
        assertThat(swipe2.get("matched").asBoolean()).isTrue();

        // Both see the match
        JsonNode matchesA = mapper.readTree(
                rest.exchange("/api/matches", HttpMethod.GET, auth(tokenA), String.class).getBody());
        assertThat(matchesA.size()).isEqualTo(1);
        assertThat(matchesA.get(0).get("userId").asLong()).isEqualTo(idB);

        // B no longer appears in A's refreshed feed (cache evicted on swipe)
        JsonNode feedAAfter = mapper.readTree(
                rest.exchange("/api/discovery/feed", HttpMethod.GET, auth(tokenA), String.class).getBody());
        assertThat(feedAAfter.findValuesAsText("id")).doesNotContain(String.valueOf(idB));
    }

    @Test
    void protectedEndpointRequiresAuth() {
        ResponseEntity<String> resp =
                rest.exchange("/api/profile/me", HttpMethod.GET, HttpEntity.EMPTY, String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private String swipe(String token, long targetId) {
        String body = """
                {"targetId":%d,"direction":"LIKE"}
                """.formatted(targetId);
        HttpHeaders headers = bearer(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForEntity("/api/swipes", new HttpEntity<>(body, headers), String.class).getBody();
    }

    private HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Void> auth(String token) {
        return new HttpEntity<>(bearer(token));
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}

package com.fitmate.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "this-is-a-test-secret-that-is-definitely-long-enough-123";

    private final JwtService jwtService = new JwtService(SECRET, 3_600_000L);

    @Test
    void generatesAndParsesTokenRoundTrip() {
        String token = jwtService.generateToken(42L, "user@fitmate.dev");

        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
        assertThat(jwtService.extractEmail(token)).isEqualTo("user@fitmate.dev");
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtService other = new JwtService("a-completely-different-secret-key-value-000000", 3_600_000L);
        String token = other.generateToken(1L, "x@y.com");

        assertThatThrownBy(() -> jwtService.parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void rejectsExpiredToken() {
        JwtService shortLived = new JwtService(SECRET, -1_000L);
        String expired = shortLived.generateToken(1L, "x@y.com");

        assertThatThrownBy(() -> jwtService.parse(expired)).isInstanceOf(JwtException.class);
    }
}

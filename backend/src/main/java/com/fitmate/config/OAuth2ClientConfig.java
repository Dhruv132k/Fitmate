package com.fitmate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the Google/GitHub client registrations ONLY when OAuth is turned on
 * ({@code fitmate.oauth2.enabled=true}) and credentials are supplied. This keeps
 * the app booting normally when no OAuth credentials are configured.
 */
@Configuration
@ConditionalOnProperty(prefix = "fitmate.oauth2", name = "enabled", havingValue = "true")
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:}") String googleClientId,
            @Value("${GOOGLE_CLIENT_SECRET:}") String googleClientSecret,
            @Value("${GITHUB_CLIENT_ID:}") String githubClientId,
            @Value("${GITHUB_CLIENT_SECRET:}") String githubClientSecret) {

        List<ClientRegistration> registrations = new ArrayList<>();

        if (StringUtils.hasText(googleClientId)) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .build());
        }
        if (StringUtils.hasText(githubClientId)) {
            registrations.add(CommonOAuth2Provider.GITHUB.getBuilder("github")
                    .clientId(githubClientId)
                    .clientSecret(githubClientSecret)
                    .scope("read:user", "user:email")
                    .build());
        }

        if (registrations.isEmpty()) {
            throw new IllegalStateException(
                    "fitmate.oauth2.enabled=true but no GOOGLE_CLIENT_ID or GITHUB_CLIENT_ID was provided.");
        }
        return new InMemoryClientRegistrationRepository(registrations);
    }
}
package com.fitmate.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** On OAuth failure, send the user back to the SPA login page with an error flag. */
@Component
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final String redirectUri;

    public OAuth2LoginFailureHandler(HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
                                     @Value("${fitmate.oauth2.redirect-uri}") String redirectUri) {
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        authorizationRequestRepository.removeAuthorizationRequest(request, response);
        String target = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", URLEncode(exception.getMessage()))
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }

    private String URLEncode(String value) {
        return java.net.URLEncoder.encode(value == null ? "login_failed" : value, StandardCharsets.UTF_8);
    }
}
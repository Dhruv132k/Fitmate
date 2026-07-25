package com.fitmate.security.oauth;

import com.fitmate.security.JwtService;
import com.fitmate.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * After the provider authenticates the user, find-or-create the FitMate account,
 * mint our own JWT, and redirect the browser back to the SPA with the token in
 * the query string. The SPA stores it and behaves exactly like a normal login.
 */
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuthAccountService accountService;
    private final JwtService jwtService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final String redirectUri;

    public OAuth2LoginSuccessHandler(OAuthAccountService accountService,
                                     JwtService jwtService,
                                     HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
                                     @Value("${fitmate.oauth2.redirect-uri}") String redirectUri) {
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.redirectUri = redirectUri;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String registrationId = token.getAuthorizedClientRegistrationId();
        OAuth2User principal = token.getPrincipal();

        User user = accountService.upsert(registrationId, principal.getAttributes());
        String jwt = jwtService.generateToken(user.getId(), user.getEmail());

        String target = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", jwt)
                .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequest(request, response);
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
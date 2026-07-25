package com.fitmate.security.oauth;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Stores the in-flight Oauth2 authorization request in a short-lived cookie
 * instead of the Http session. THis is what lets social login work while the
 * rest of the API stays fully stateless (no server-side sessions).
 */

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest>{

    public static final String OAUTH2_REQUEST_COOKIE = "FITMATE_OAUTH2_REQUEST";
    public static final int COOKIE_MAX_AGE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_REQUEST_COOKIE)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        CookieUtils.deleteCookie(request, response, OAUTH2_REQUEST_COOKIE);
        return authorizationRequest;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if(authorizationRequest==null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_REQUEST_COOKIE);
            return;
        }
        CookieUtils.addCookie(response, OAUTH2_REQUEST_COOKIE, CookieUtils.serialize(authorizationRequest), COOKIE_MAX_AGE_SECONDS);
    }
    
}

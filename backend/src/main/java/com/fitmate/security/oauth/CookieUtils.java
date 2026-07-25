package com.fitmate.security.oauth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* Small helpers for reading/writing short-lived cppkies used during the OAuth dance */
public final class CookieUtils {
    
    private CookieUtils() {
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(name)) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    public static void addCookie (HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        getCookie(request, name).ifPresent(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        });
    }

    public static String serialize(Serializable object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            oos.flush();
            return Base64.getUrlEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to serialze object to cookie", e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> type) {
        byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return type.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to deserialize cookie value", e);
        }
    }
}

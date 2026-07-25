package com.fitmate.security.oauth;

import com.fitmate.common.BadRequestException;
import com.fitmate.user.AuthProvider;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.WorkoutGoal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Turns a verified social-login profile into a FitMate {@link User}:
 * links to an existing account (by provider id, or by email) or creates a new
 * one. New social users get a default workout goal they can edit later.
 */
@Service
public class OAuthAccountService {

    private final UserRepository userRepository;

    public OAuthAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User upsert(String registrationId, Map<String, Object> attributes) {
        AuthProvider provider = toProvider(registrationId);
        String providerId = extractProviderId(provider, attributes);
        String email = extractEmail(provider, providerId, attributes);
        String name = extractName(provider, attributes);

        // 1. Already linked to this provider account?
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existing -> updateName(existing, name))
                // 2. Existing local/other account with the same email? Link it.
                .or(() -> userRepository.findByEmailIgnoreCase(email)
                        .map(existing -> {
                            existing.setProvider(provider);
                            existing.setProviderId(providerId);
                            return updateName(existing, name);
                        }))
                // 3. Brand-new social user.
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .providerId(providerId)
                        .passwordHash("{oauth2}" + UUID.randomUUID())
                        .workoutGoal(WorkoutGoal.GENERAL_FITNESS)
                        .active(true)
                        .build()));
    }

    private User updateName(User user, String name) {
        if (StringUtils.hasText(name) && !name.equals(user.getName())) {
            user.setName(name);
        }
        return userRepository.save(user);
    }

    private AuthProvider toProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> throw new BadRequestException("Unsupported OAuth provider: " + registrationId);
        };
    }

    private String extractProviderId(AuthProvider provider, Map<String, Object> attributes) {
        Object id = provider == AuthProvider.GOOGLE ? attributes.get("sub") : attributes.get("id");
        if (id == null) {
            throw new BadRequestException("OAuth provider did not return a user id");
        }
        return String.valueOf(id);
    }

    private String extractEmail(AuthProvider provider, String providerId, Map<String, Object> attributes) {
        Object email = attributes.get("email");
        if (email != null && StringUtils.hasText(email.toString())) {
            return email.toString().toLowerCase();
        }
        // GitHub users with a private email won't expose one; synthesize a stable, unique address.
        String handle = provider == AuthProvider.GITHUB && attributes.get("login") != null
                ? attributes.get("login").toString()
                : providerId;
        return (handle + "@" + provider.name().toLowerCase() + ".fitmate.local").toLowerCase();
    }

    private String extractName(AuthProvider provider, Map<String, Object> attributes) {
        Object name = attributes.get("name");
        if (name != null && StringUtils.hasText(name.toString())) {
            return name.toString();
        }
        Object login = attributes.get("login"); // GitHub username fallback
        return login != null ? login.toString() : "FitMate User";
    }
}
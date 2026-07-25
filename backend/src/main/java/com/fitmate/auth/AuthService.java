package com.fitmate.auth;

import com.fitmate.auth.dto.AuthResponse;
import com.fitmate.auth.dto.LoginRequest;
import com.fitmate.auth.dto.RegisterRequest;
import com.fitmate.common.ConflictException;
import com.fitmate.security.JwtService;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.dto.UserProfileResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = User.builder()
                .email(req.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.password()))
                .name(req.name())
                .age(req.age())
                .bio(req.bio())
                .workoutGoal(req.workoutGoal())
                .experienceLevel(req.experienceLevel())
                .gymName(req.gymName())
                .city(req.city())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .active(true)
                .build();
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthResponse.of(token, UserProfileResponse.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return AuthResponse.of(token, UserProfileResponse.from(user));
    }
}

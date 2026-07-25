package com.fitmate.auth;

import com.fitmate.auth.dto.AuthResponse;
import com.fitmate.auth.dto.LoginRequest;
import com.fitmate.auth.dto.RegisterRequest;
import com.fitmate.common.ConflictException;
import com.fitmate.security.JwtService;
import com.fitmate.user.User;
import com.fitmate.user.UserRepository;
import com.fitmate.user.WorkoutGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;

    @InjectMocks AuthService authService;

    private RegisterRequest registerRequest() {
        return new RegisterRequest("New@User.com", "password123", "New User",
                WorkoutGoal.MUSCLE_GAIN, null, "Iron Paradise", "Pune", 25, "hi", null, null);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("New@User.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void registerHashesPasswordLowercasesEmailAndReturnsToken() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(1L, "new@user.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest());

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.user().email()).isEqualTo("new@user.com");
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = User.builder().id(1L).email("a@b.com").passwordHash("HASH").build();
        when(userRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("a@b.com", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        User user = User.builder().id(1L).email("a@b.com").passwordHash("HASH")
                .name("A").workoutGoal(WorkoutGoal.STRENGTH).build();
        when(userRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("right", "HASH")).thenReturn(true);
        when(jwtService.generateToken(1L, "a@b.com")).thenReturn("jwt");

        AuthResponse response = authService.login(new LoginRequest("a@b.com", "right"));

        assertThat(response.token()).isEqualTo("jwt");
    }
}

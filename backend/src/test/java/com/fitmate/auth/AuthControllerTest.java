package com.fitmate.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitmate.auth.dto.AuthResponse;
import com.fitmate.common.ConflictException;
import com.fitmate.common.GlobalExceptionHandler;
import com.fitmate.user.WorkoutGoal;
import com.fitmate.user.dto.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Standalone MockMvc test - exercises the controller + bean validation +
 * {@link GlobalExceptionHandler} without loading the security/JWT context.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock AuthService authService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private AuthResponse sampleResponse() {
        UserProfileResponse profile = new UserProfileResponse(
                1L, "new@user.com", "New User", 25, "hi",
                WorkoutGoal.MUSCLE_GAIN, null, "Iron Paradise", "Pune", null, null, Instant.now());
        return AuthResponse.of("jwt-token", profile);
    }

    @Test
    void registerReturns201WithToken() throws Exception {
        when(authService.register(any())).thenReturn(sampleResponse());
        Map<String, Object> req = Map.of(
                "email", "new@user.com", "password", "password123",
                "name", "New User", "workoutGoal", "MUSCLE_GAIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.email").value("new@user.com"));
    }

    @Test
    void registerReturns400OnInvalidPayload() throws Exception {
        Map<String, Object> bad = Map.of(
                "email", "not-an-email", "password", "short", "name", "", "workoutGoal", "MUSCLE_GAIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    void registerReturns409OnDuplicateEmail() throws Exception {
        when(authService.register(any())).thenThrow(new ConflictException("exists"));
        Map<String, Object> req = Map.of(
                "email", "dup@user.com", "password", "password123",
                "name", "Dup", "workoutGoal", "MUSCLE_GAIN");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturns200WithToken() throws Exception {
        when(authService.login(any())).thenReturn(sampleResponse());
        Map<String, Object> req = Map.of("email", "new@user.com", "password", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}

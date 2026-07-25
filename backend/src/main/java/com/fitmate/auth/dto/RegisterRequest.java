package com.fitmate.auth.dto;

import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.WorkoutGoal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String name,
        @NotNull WorkoutGoal workoutGoal,
        ExperienceLevel experienceLevel,
        @Size(max = 150) String gymName,
        @Size(max = 100) String city,
        Integer age,
        @Size(max = 500) String bio,
        Double latitude,
        Double longitude
) {
}

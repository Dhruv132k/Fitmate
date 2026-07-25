package com.fitmate.user.dto;

import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.WorkoutGoal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 1, max = 100) String name,
        @Min(13) @Max(120) Integer age,
        @Size(max = 500) String bio,
        WorkoutGoal workoutGoal,
        ExperienceLevel experienceLevel,
        @Size(max = 150) String gymName,
        @Size(max = 100) String city,
        Double latitude,
        Double longitude
) {
}

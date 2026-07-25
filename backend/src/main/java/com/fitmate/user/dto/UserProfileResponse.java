package com.fitmate.user.dto;

import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.User;
import com.fitmate.user.WorkoutGoal;

import java.time.Instant;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        Integer age,
        String bio,
        WorkoutGoal workoutGoal,
        ExperienceLevel experienceLevel,
        String gymName,
        String city,
        Double latitude,
        Double longitude,
        Instant createdAt
) {
    public static UserProfileResponse from(User u) {
        return new UserProfileResponse(
                u.getId(), u.getEmail(), u.getName(), u.getAge(), u.getBio(),
                u.getWorkoutGoal(), u.getExperienceLevel(), u.getGymName(),
                u.getCity(), u.getLatitude(), u.getLongitude(), u.getCreatedAt());
    }
}

package com.fitmate.match.dto;

import com.fitmate.user.WorkoutGoal;

import java.time.Instant;

public record MatchResponse(
        Long matchId,
        Long userId,
        String name,
        Integer age,
        String bio,
        WorkoutGoal workoutGoal,
        String gymName,
        String city,
        Instant matchedAt
) {
}

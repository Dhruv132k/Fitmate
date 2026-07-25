package com.fitmate.discovery.dto;

import com.fitmate.user.ExperienceLevel;
import com.fitmate.user.WorkoutGoal;

import java.io.Serializable;
import java.util.List;

/**
 * A single card shown in the swipe deck. {@code matchReasons} explains why this
 * person surfaced (shared goal, same gym, same city) to power the UI.
 * Implements Serializable so it can be stored in the Redis-backed feed cache.
 */
public record CandidateCard(
        Long id,
        String name,
        Integer age,
        String bio,
        WorkoutGoal workoutGoal,
        ExperienceLevel experienceLevel,
        String gymName,
        String city,
        List<String> matchReasons,
        int compatibilityScore
) implements Serializable {
}

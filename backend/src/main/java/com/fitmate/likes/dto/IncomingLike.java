package com.fitmate.likes.dto;

import java.time.Instant;

import com.fitmate.user.WorkoutGoal;

/*A person who has liked the current user but hasn't been swiped back yet */
public record IncomingLike(
    Long userId,
    String name,
    Integer age,
    String bio,
    WorkoutGoal workoutGoal,
    String gymName,
    String city,
    Instant likedAt
) {
}

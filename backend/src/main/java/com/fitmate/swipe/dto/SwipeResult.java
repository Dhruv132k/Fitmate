package com.fitmate.swipe.dto;

public record SwipeResult(
        boolean matched,
        Long matchId,
        String message
) {
    public static SwipeResult noMatch() {
        return new SwipeResult(false, null, "Swipe recorded");
    }

    public static SwipeResult matched(Long matchId) {
        return new SwipeResult(true, matchId, "It's a match!");
    }
}

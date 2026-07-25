package com.fitmate.swipe.dto;

import com.fitmate.swipe.SwipeDirection;
import jakarta.validation.constraints.NotNull;

public record SwipeRequest(
        @NotNull Long targetId,
        @NotNull SwipeDirection direction
) {
}

package com.fitmate.swipe;

import com.fitmate.security.CurrentUser;
import com.fitmate.swipe.dto.SwipeRequest;
import com.fitmate.swipe.dto.SwipeResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/swipes")
public class SwipeController {

    private final SwipeService swipeService;

    public SwipeController(SwipeService swipeService) {
        this.swipeService = swipeService;
    }

    @PostMapping
    public ResponseEntity<SwipeResult> swipe(@Valid @RequestBody SwipeRequest request) {
        return ResponseEntity.ok(swipeService.swipe(CurrentUser.id(), request));
    }
}

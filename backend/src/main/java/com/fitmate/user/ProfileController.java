package com.fitmate.user;

import com.fitmate.security.CurrentUser;
import com.fitmate.user.dto.UpdateProfileRequest;
import com.fitmate.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me() {
        return ResponseEntity.ok(userService.getProfile(CurrentUser.id()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> update(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(CurrentUser.id(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> byId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}

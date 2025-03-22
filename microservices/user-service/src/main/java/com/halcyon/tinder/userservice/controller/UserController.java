package com.halcyon.tinder.userservice.controller;

import com.halcyon.tinder.userservice.dto.user.UserProfileDto;
import com.halcyon.tinder.userservice.dto.user.UserPutRequest;
import com.halcyon.tinder.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        UserProfileDto profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable UUID userId) {
        UserProfileDto profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> update(@RequestBody @Valid UserPutRequest userPutRequest) {
        UserProfileDto profile = userService.update(userPutRequest);
        return ResponseEntity.ok(profile);
    }
}

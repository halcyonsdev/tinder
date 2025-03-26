package com.halcyon.tinder.userservice.controller;

import com.halcyon.tinder.userservice.dto.user.UserProfileDto;
import com.halcyon.tinder.userservice.dto.user.UserPutRequest;
import com.halcyon.tinder.userservice.service.UserService;
import com.halcyon.tinder.userservice.service.support.ImageData;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/avatar")
    public ResponseEntity<UserProfileDto> uploadAvatar(@RequestParam("image") MultipartFile image) {
        UserProfileDto profile = userService.uploadAvatar(image);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<UserProfileDto> deleteAvatar() {
        UserProfileDto profile = userService.deleteAvatar();
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/image/{imageName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String imageName) {
        ImageData imageData = userService.downloadImage(imageName);

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + imageData.imageName());

        return ResponseEntity.ok()
                .contentType(imageData.mediaType())
                .headers(headers)
                .body(imageData.resource());
    }

    @PostMapping("/gallery")
    public ResponseEntity<UserProfileDto> uploadGallery(@RequestParam("image") List<MultipartFile> images) {
        UserProfileDto profile = userService.uploadGalleryImages(images);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{userId}/gallery")
    public ResponseEntity<List<String>> getUserGallery(@PathVariable UUID userId) {
        List<String> gallery = userService.getGallery(userId);
        return ResponseEntity.ok(gallery);
    }

    @DeleteMapping("/gallery/{imageName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGalleryImage(@PathVariable String imageName) {
        userService.deleteGalleryImage(imageName);
    }
}

package com.halcyon.tinder.authservice.controller;

import com.halcyon.tinder.authservice.dto.auth.AuthenticationResponse;
import com.halcyon.tinder.authservice.dto.auth.RefreshRequest;
import com.halcyon.tinder.authservice.dto.auth.SignInRequest;
import com.halcyon.tinder.authservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        AuthenticationResponse response = authenticationService.signUp(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        AuthenticationResponse response = authenticationService.signIn(signInRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
        authenticationService.logout();
    }

    @PostMapping("/access")
    public ResponseEntity<AuthenticationResponse> getAccessToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthenticationResponse response = authenticationService.getTokensByRefresh(refreshRequest.getRefreshToken(), false);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> getRefreshToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthenticationResponse response = authenticationService.getTokensByRefresh(refreshRequest.getRefreshToken(), true);
        return ResponseEntity.ok(response);
    }
}

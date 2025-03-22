package com.halcyon.tinder.userservice.controller;

import com.halcyon.tinder.userservice.dto.auth.AuthenticationResponse;
import com.halcyon.tinder.userservice.dto.auth.RefreshRequest;
import com.halcyon.tinder.userservice.dto.auth.SignInRequest;
import com.halcyon.tinder.userservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.userservice.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        AuthenticationResponse authenticationResponse = authService.signUp(signUpRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody @Valid SignInRequest signInRequest) {
        AuthenticationResponse authenticationResponse = authService.signIn(signInRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/access")
    public ResponseEntity<AuthenticationResponse> getAccessToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthenticationResponse authenticationResponse = authService.getTokensByRefresh(refreshRequest.getRefreshToken(), false);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> getRefreshToken(@RequestBody @Valid RefreshRequest refreshRequest) {
        AuthenticationResponse authenticationResponse = authService.getTokensByRefresh(refreshRequest.getRefreshToken(), true);
        return ResponseEntity.ok(authenticationResponse);
    }
}

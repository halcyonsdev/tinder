package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.userservice.dto.auth.AuthenticationResponse;
import com.halcyon.tinder.userservice.dto.auth.SignInRequest;
import com.halcyon.tinder.userservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.userservice.exception.InvalidCredentialsException;
import com.halcyon.tinder.userservice.exception.InvalidTokenException;
import com.halcyon.tinder.userservice.exception.UserAlreadyExistsException;
import com.halcyon.tinder.userservice.mapper.UserMapper;
import com.halcyon.tinder.userservice.model.User;
import com.halcyon.tinder.userservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final TokenRevocationService tokenRevocationService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse signUp(SignUpRequest signUpRequest) {
        if (userService.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new UserAlreadyExistsException("User with phone " + signUpRequest.getPhoneNumber() + " already exists");
        }

        User user = userMapper.toEntity(signUpRequest, passwordEncoder);
        userService.save(user);

        return getAuthResponse(user.getPhoneNumber());
    }

    private AuthenticationResponse getAuthResponse(String phoneNumber) {
        String accessToken = jwtProvider.generateToken(phoneNumber, false);
        String refreshToken = jwtProvider.generateToken(phoneNumber, true);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse signIn(SignInRequest signInRequest) {
        User user = userService.findByPhoneNumber(signInRequest.getPhoneNumber());

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid phone number or password");
        }

        return getAuthResponse(user.getPhoneNumber());
    }

    public void logout() {
        String jwtToken = jwtProvider.getJwtAuthentication().getToken();
        tokenRevocationService.revokeToken(jwtToken);
    }

    public AuthenticationResponse getTokensByRefresh(String refreshToken, boolean isRefresh) {
        if (!jwtProvider.isValidToken(refreshToken, true)) {
            throw new InvalidTokenException("Refresh token is invalid");
        }

        String subject = jwtProvider.extractAllClaims(refreshToken, true).getSubject();
        User user = userService.findByPhoneNumber(subject);

        String accessToken = jwtProvider.generateToken(user.getPhoneNumber(), false);
        String newRefreshToken = isRefresh
                ? jwtProvider.generateToken(user.getPhoneNumber(), true)
                : null;

        return new AuthenticationResponse(accessToken, newRefreshToken);
    }
}

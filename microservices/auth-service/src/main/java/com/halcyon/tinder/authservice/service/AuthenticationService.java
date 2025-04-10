package com.halcyon.tinder.authservice.service;

import com.halcyon.tinder.authservice.dto.auth.AuthenticationResponse;
import com.halcyon.tinder.authservice.dto.auth.SignInRequest;
import com.halcyon.tinder.authservice.dto.auth.SignUpRequest;
import com.halcyon.tinder.authservice.dto.user.CreateUserRequest;
import com.halcyon.tinder.authservice.exception.InvalidCredentialsException;
import com.halcyon.tinder.authservice.exception.InvalidTokenException;
import com.halcyon.tinder.authservice.exception.UserAlreadyExistsException;
import com.halcyon.tinder.authservice.mapper.UserMapper;
import com.halcyon.tinder.jwtcore.JwtProvider;
import com.halcyon.tinder.jwtcore.TokenRevocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import user.User;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final TokenRevocationService tokenRevocationService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserActionsProducer userActionsProducer;
    private final UserMapper userMapper;
    private final UserServiceGrpcClient userServiceGrpcClient;

    public AuthenticationResponse signUp(SignUpRequest signUpRequest) {
        if (userServiceGrpcClient.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new UserAlreadyExistsException("User with phone " + signUpRequest.getPhoneNumber() + " already exists");
        }

        CreateUserRequest createUserRequest = userMapper.toCreateUserRequest(signUpRequest, passwordEncoder);
        userActionsProducer.executeCreatingUser(createUserRequest);

        return getAuthResponse(signUpRequest.getPhoneNumber());
    }

    private AuthenticationResponse getAuthResponse(String phoneNumber) {
        String accessToken = jwtProvider.generateToken(phoneNumber, false);
        String refreshToken = jwtProvider.generateToken(phoneNumber, true);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse signIn(SignInRequest signInRequest) {
        User.UserResponse user = userServiceGrpcClient.getByPhoneNumber(signInRequest.getPhoneNumber());

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
            throw new InvalidTokenException("Invalid refresh token");
        }

        String subject = jwtProvider.extractAllClaims(refreshToken, true).getSubject();
        User.UserResponse user = userServiceGrpcClient.getByPhoneNumber(subject);

        String accessToken = jwtProvider.generateToken(user.getPhoneNumber(), false);
        String newRefreshToken = isRefresh
                ? jwtProvider.generateToken(user.getPhoneNumber(), true)
                : null;

        return new AuthenticationResponse(accessToken, newRefreshToken);
    }
}

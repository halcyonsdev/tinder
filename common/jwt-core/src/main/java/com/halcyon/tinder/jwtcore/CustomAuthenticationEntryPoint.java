package com.halcyon.tinder.jwtcore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.halcyon.tinder.exceptioncore.ErrorDetailsResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private static final String INVALID_TOKEN_MESSAGE = "Token is missing, invalid, expired, or revoked";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        var errorDetails = ErrorDetailsResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("InvalidToken")
                .message(INVALID_TOKEN_MESSAGE)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        OutputStream responseStream = response.getOutputStream();

        objectMapper.writeValue(responseStream, errorDetails);
        responseStream.flush();
    }
}

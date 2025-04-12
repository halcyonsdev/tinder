package com.halcyon.tinder.jwtcore

import com.fasterxml.jackson.databind.ObjectMapper
import com.halcyon.tinder.exceptioncore.ErrorDetailsResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val errorDetails = ErrorDetailsResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("InvalidToken")
            .message(INVALID_TOKEN_MESSAGE)
            .build()

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        response.outputStream.use { outputStream ->
            objectMapper.writeValue(outputStream, errorDetails)
        }
    }

    companion object {
        private const val INVALID_TOKEN_MESSAGE = "Token is missing, invalid, expired, or revoked"
    }
}
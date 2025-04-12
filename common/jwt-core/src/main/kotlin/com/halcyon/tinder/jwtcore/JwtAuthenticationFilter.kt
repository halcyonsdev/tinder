package com.halcyon.tinder.jwtcore

import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val tokenRevocationService: TokenRevocationService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtToken = getTokenFromRequest(request)
        val isRefresh = false

        if (jwtToken != null && jwtProvider.isValidToken(jwtToken, isRefresh) && !tokenRevocationService.isRevoked(jwtToken)) {
            val claims: Claims = jwtProvider.extractAllClaims(jwtToken, isRefresh)
            val jwtAuthentication = JwtAuthentication(true, claims.subject, jwtToken)

            SecurityContextHolder.getContext().authentication = jwtAuthentication
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)

        return if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            authHeader.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
}
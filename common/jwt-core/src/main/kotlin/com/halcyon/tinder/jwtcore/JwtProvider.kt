package com.halcyon.tinder.jwtcore

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret.access}") private val accessToken: String,
    @Value("\${jwt.secret.refresh}") private val refreshToken: String
) {

    private val accessKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessToken))
    private val refreshKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshToken))

    fun generateToken(phoneNumber: String, isRefresh: Boolean): String {
        val now = LocalDateTime.now()
        val jti = UUID.randomUUID().toString()

        val expirationInstant = if (isRefresh) {
            now.plusDays(31).atZone(ZoneId.systemDefault()).toInstant()
        } else {
            now.plusDays(7).atZone(ZoneId.systemDefault()).toInstant()
        }

        return Jwts.builder()
            .id(jti)
            .subject(phoneNumber)
            .issuedAt(Date())
            .expiration(Date.from(expirationInstant))
            .signWith(if (isRefresh) refreshKey else accessKey)
            .compact()
    }

    fun extractAllClaims(jwtToken: String, isRefresh: Boolean): Claims {
        return Jwts.parser()
            .verifyWith(if (isRefresh) refreshKey else accessKey)
            .build()
            .parseSignedClaims(jwtToken)
            .payload
    }

    fun isValidToken(jwtToken: String, isRefresh: Boolean): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(if (isRefresh) refreshKey else accessKey)
                .build()
                .parseSignedClaims(jwtToken)
            true
        } catch (ignored: Exception) {
            false
        }
    }

    fun getRemainingTokenLifetime(jwtToken: String): Long {
        val expiration = extractAllClaims(jwtToken, false).expiration
        return (expiration.time - System.currentTimeMillis())
    }

    fun getJwtAuthentication(): JwtAuthentication {
        return SecurityContextHolder.getContext().authentication as JwtAuthentication
    }
}
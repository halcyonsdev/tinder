package com.halcyon.tinder.jwtcore

import com.halcyon.tinder.rediscache.CacheManager
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenRevocationService(
    private val cacheManager: CacheManager,
    private val jwtProvider: JwtProvider
) {

    companion object {
        private const val TOKEN_PREFIX = "revoked-token:"
    }

    fun revokeToken(jwtToken: String) {
        val expiryInSeconds = jwtProvider.getRemainingTokenLifetime(jwtToken)
        val key = TOKEN_PREFIX + jwtToken

        cacheManager.save(key, Duration.ofSeconds(expiryInSeconds))
    }

    fun isRevoked(jwtToken: String): Boolean {
        val key = TOKEN_PREFIX + jwtToken
        return cacheManager.isPresent(key)
    }
}
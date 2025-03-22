package com.halcyon.tinder.userservice.service;

import com.halcyon.tinder.rediscache.CacheManager;
import com.halcyon.tinder.userservice.security.JwtProvider;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private final CacheManager cacheManager;
    private final JwtProvider jwtProvider;

    private static final String TOKEN_PREFIX = "revoked-token:";

    public void revokeToken(String jwtToken) {
        long expiryInSeconds = jwtProvider.getRemainingTokenLifetime(jwtToken);
        String key = TOKEN_PREFIX + jwtToken;

        cacheManager.save(key, Duration.ofSeconds(expiryInSeconds));
    }

    public boolean isRevoked(String jwtToken) {
        String key = TOKEN_PREFIX + jwtToken;
        return cacheManager.isPresent(key);
    }
}

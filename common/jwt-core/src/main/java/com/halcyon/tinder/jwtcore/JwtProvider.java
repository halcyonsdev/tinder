package com.halcyon.tinder.jwtcore;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public JwtProvider(
                       @Value("${jwt.secret.access}") String accessToken,
                       @Value("${jwt.secret.refresh}") String refreshToken) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessToken));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshToken));
    }

    public String generateToken(String phoneNumber, boolean isRefresh) {
        var now = LocalDateTime.now();
        String jti = UUID.randomUUID().toString();

        Instant expirationInstant = isRefresh
                ? now.plusDays(31).atZone(ZoneId.systemDefault()).toInstant()
                : now.plusDays(7).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .id(jti)
                .subject(phoneNumber)
                .issuedAt(new Date())
                .expiration(Date.from(expirationInstant))
                .signWith(isRefresh
                        ? refreshKey
                        : accessKey)
                .compact();
    }

    public Claims extractAllClaims(String jwtToken, boolean isRefresh) {
        return Jwts.parser()
                .verifyWith(isRefresh
                        ? refreshKey
                        : accessKey)
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    public boolean isValidToken(String jwtToken, boolean isRefresh) {
        try {
            Jwts.parser()
                    .verifyWith(isRefresh
                            ? refreshKey
                            : accessKey)
                    .build()
                    .parseSignedClaims(jwtToken);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public long getRemainingTokenLifetime(String jwtToken) {
        Claims claims = extractAllClaims(jwtToken, false);
        Date expiration = claims.getExpiration();
        return (expiration.getTime() - System.currentTimeMillis()) / 1000;
    }

    public JwtAuthentication getJwtAuthentication() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}

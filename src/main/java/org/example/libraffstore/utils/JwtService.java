package org.example.libraffstore.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshExpiration) {

        this.accessKey  = Keys.hmacShaKeyFor(("ACCESS-"  + secret).getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(("REFRESH-" + secret).getBytes(StandardCharsets.UTF_8));
        this.accessExpiration  = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(), accessKey, accessExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(), refreshKey, refreshExpiration);
    }

    private String buildToken(String subject, SecretKey key, long expiration) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        return validate(token, userDetails, accessKey);
    }

    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        return validate(token, userDetails, refreshKey);
    }

    private boolean validate(String token, UserDetails userDetails, SecretKey key) {
        try {
            String username = extractUsername(token, key);
            return username.equals(userDetails.getUsername()) && !isExpired(token, key);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsernameFromAccessToken(String token) {
        return extractUsername(token, accessKey);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractUsername(token, refreshKey);
    }

    private String extractUsername(String token, SecretKey key) {
        return extractClaim(token, key, Claims::getSubject);
    }

    private boolean isExpired(String token, SecretKey key) {
        return extractClaim(token, key, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, SecretKey key, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}
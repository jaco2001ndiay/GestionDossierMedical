package com.medical.GestionDossierMedical.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Component
public class JwtUtil {

    private final SecretKey jwtSecret;
    private final long jwtExpirationMs = 24 * 60 * 60 * 1000;       // 24h
    private final long refreshExpirationMs = 7L * jwtExpirationMs;  // 7 jours

    public JwtUtil() {
        String secret = System.getenv("JWT_SECRET") != null
                ? System.getenv("JWT_SECRET")
                : generateSecretKey();
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public String generateToken(String username, Long userId, Set<String> roles) {
        Map<String, Object> claims = Map.of(
                "userId", userId,
                "roles", roles
        );
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String username, Long userId, Set<String> roles) {
        Map<String, Object> claims = Map.of(
                "userId", userId,
                "roles", roles
        );
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Long getUserIdFromToken(String token) {
        Object id = getAllClaims(token).get("userId");
        return id == null ? null : Long.valueOf(id.toString());
    }

    public Set<String> getRolesFromToken(String token) {
        Object rolesObj = getAllClaims(token).get("roles");
        if (rolesObj instanceof Iterable<?>) {
            Set<String> roles = new java.util.HashSet<>();
            for (Object role : (Iterable<?>) rolesObj) {
                roles.add(role.toString());
            }
            return roles;
        }
        return Set.of();
    }


    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

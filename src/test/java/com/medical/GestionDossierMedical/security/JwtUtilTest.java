package com.medical.GestionDossierMedical.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        // Crée une instance avec une clé secrète stable pour les tests
        String fakeSecret = "test-secret-key-test-secret-key-test-key";
        System.setProperty("JWT_SECRET", fakeSecret); // simule l'env var
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateAndValidateAccessToken() {
        String username = "testuser";
        Long userId = 42L;
        Set<String> roles = Set.of("MEDECIN");

        String token = jwtUtil.generateToken(username, userId, roles);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals(username, jwtUtil.getUsernameFromToken(token));
        assertEquals(userId, jwtUtil.getUserIdFromToken(token));
        assertEquals(roles, jwtUtil.getRolesFromToken(token));
    }

    @Test
    void generateAndValidateRefreshToken() {
        String username = "refreshtest";
        Long userId = 99L;
        Set<String> roles = Set.of("ADMIN");

        String refreshToken = jwtUtil.generateRefreshToken(username, userId, roles);

        assertNotNull(refreshToken);
        assertTrue(jwtUtil.validateToken(refreshToken));
        assertEquals(username, jwtUtil.getUsernameFromToken(refreshToken));
        assertEquals(userId, jwtUtil.getUserIdFromToken(refreshToken));
        assertEquals(roles, jwtUtil.getRolesFromToken(refreshToken));
    }

    @Test
    void invalidTokenShouldBeRejected() {
        String fakeToken = "invalid.token.structure";

        assertFalse(jwtUtil.validateToken(fakeToken));
    }
}

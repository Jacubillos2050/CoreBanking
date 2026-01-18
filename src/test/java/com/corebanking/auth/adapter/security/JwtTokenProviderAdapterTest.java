package com.corebanking.auth.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderAdapterTest {

    private JwtTokenProviderAdapter jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Use test values
        jwtTokenProvider = new JwtTokenProviderAdapter("testSecretKeyForJwtTokenGenerationAndValidation", 3600000L);
    }

    @Test
    void generateToken_Success() {
        // When
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_Valid() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void getRoleFromToken_Valid() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When
        String role = jwtTokenProvider.getRoleFromToken(token);

        // Then
        assertEquals("USER", role);
    }

    @Test
    void validateToken_Valid() {
        // Given
        String token = jwtTokenProvider.generateToken("testuser", "USER");

        // When
        boolean valid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(valid);
    }

    @Test
    void validateToken_Invalid() {
        // When
        boolean valid = jwtTokenProvider.validateToken("invalidtoken");

        // Then
        assertFalse(valid);
    }

    @Test
    void validateToken_Expired() {
        // Create with short expiration
        JwtTokenProviderAdapter shortLived = new JwtTokenProviderAdapter("testSecretKeyForJwtTokenGenerationAndValidation", 1L);
        String token = shortLived.generateToken("testuser", "USER");

        // Wait for expiration
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean valid = shortLived.validateToken(token);

        // Then
        assertFalse(valid);
    }
}
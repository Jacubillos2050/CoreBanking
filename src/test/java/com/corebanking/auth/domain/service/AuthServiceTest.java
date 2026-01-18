package com.corebanking.auth.domain.service;

import com.corebanking.auth.domain.model.User;
import com.corebanking.auth.domain.port.JwtTokenProviderPort;
import com.corebanking.auth.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProviderPort jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        User savedUser = new User(1L, "testuser", "encoded", "USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = authService.register("testuser", "password", "USER");

        // Then
        assertEquals(savedUser, result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UserExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register("testuser", "password", "USER"));
        assertEquals("user.exists", exception.getMessage());
    }

    @Test
    void validateCredentials_Valid() {
        // Given
        User user = new User(1L, "testuser", "encoded", "USER");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);

        // When
        boolean result = authService.validateCredentials("testuser", "password");

        // Then
        assertTrue(result);
    }

    @Test
    void validateCredentials_InvalidPassword() {
        // Given
        User user = new User(1L, "testuser", "encoded", "USER");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        // When
        boolean result = authService.validateCredentials("testuser", "wrong");

        // Then
        assertFalse(result);
    }

    @Test
    void validateCredentials_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        boolean result = authService.validateCredentials("nonexistent", "password");

        // Then
        assertFalse(result);
    }

    @Test
    void authenticate_Success() {
        // Given
        User user = new User(1L, "testuser", "encoded", "USER");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser", "USER")).thenReturn("token");

        // When
        String result = authService.authenticate("testuser", "password");

        // Then
        assertEquals("token", result);
    }

    @Test
    void authenticate_InvalidCredentials() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User(1L, "testuser", "encoded", "USER")));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("testuser", "wrong"));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
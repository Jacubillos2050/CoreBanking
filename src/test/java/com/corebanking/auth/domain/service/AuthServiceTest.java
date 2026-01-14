package com.corebanking.auth.domain.service;

import com.corebanking.auth.domain.model.User;
import com.corebanking.auth.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnUserWhenValid() {
        // Given
        when(userRepository.existsByUsername("juan")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0)); // Devuelve el User pasado

        // When
        User user = authService.register("juan", "password", "CUSTOMER");

        // Then
        assertNotNull(user);
        assertEquals("juan", user.getUsername());
        assertEquals("CUSTOMER", user.getRole());
        verify(userRepository).save(any());
    }

    @Test
    void register_ShouldThrowExceptionIfUserExists() {
        // Given
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authService.register("juan", "password", "CUSTOMER")
        );

        assertEquals("user.exists", exception.getMessage());
    }
}
package com.corebanking.auth.adapter.rest;

import com.corebanking.auth.domain.model.User;
import com.corebanking.auth.domain.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private AuthController controller;

    @Test
    void register_Success() {
        // Given
        User user = new User(1L, "testuser", "encodedpass", "USER");
        when(authService.register("testuser", "password", "USER")).thenReturn(user);
        AuthController.RegisterRequest request = new AuthController.RegisterRequest("testuser", "password", "USER");

        // When
        ResponseEntity<Object> result = controller.register(request, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof AuthController.UserResponse);
        AuthController.UserResponse response = (AuthController.UserResponse) result.getBody();
        assertEquals(1L, response.id());
        assertEquals("testuser", response.username());
        assertEquals("USER", response.role());
    }

    @Test
    void register_UserExists() {
        // Given
        when(authService.register("testuser", "password", "USER")).thenThrow(new IllegalArgumentException("user.exists"));
        when(messageSource.getMessage("user.exists", null, "Unknown error", Locale.ENGLISH)).thenReturn("User already exists");
        AuthController.RegisterRequest request = new AuthController.RegisterRequest("testuser", "password", "USER");

        // When
        ResponseEntity<Object> result = controller.register(request, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof AuthController.ErrorResponse);
        AuthController.ErrorResponse response = (AuthController.ErrorResponse) result.getBody();
        assertEquals("User already exists", response.message());
    }

    @Test
    void register_WithAcceptLanguage() {
        // Given
        User user = new User(1L, "testuser", "encodedpass", "USER");
        when(authService.register("testuser", "password", "USER")).thenReturn(user);
        AuthController.RegisterRequest request = new AuthController.RegisterRequest("testuser", "password", "USER");

        // When
        ResponseEntity<Object> result = controller.register(request, "es");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void login_Success() {
        // Given
        when(authService.authenticate("testuser", "password")).thenReturn("token123");
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "password");

        // When
        ResponseEntity<AuthController.LoginResponse> result = controller.login(request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("token123", result.getBody().token());
    }

    @Test
    void login_InvalidCredentials() {
        // Given
        when(authService.authenticate("testuser", "wrongpass")).thenThrow(new IllegalArgumentException("Invalid credentials"));
        AuthController.LoginRequest request = new AuthController.LoginRequest("testuser", "wrongpass");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> controller.login(request));
    }
}
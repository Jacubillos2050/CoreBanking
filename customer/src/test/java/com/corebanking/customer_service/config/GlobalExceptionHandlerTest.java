package com.corebanking.customer_service.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void handleValidationExceptions_WithTranslation() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name cannot be blank");
        FieldError fieldError2 = new FieldError("object", "email", "Email must be valid");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(messageSource.getMessage("Name cannot be blank", null, Locale.ENGLISH))
            .thenReturn("El nombre no puede estar vacío");
        when(messageSource.getMessage("Email must be valid", null, Locale.ENGLISH))
            .thenReturn("El email debe ser válido");

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("El nombre no puede estar vacío", errors.get("name"));
        assertEquals("El email debe ser válido", errors.get("email"));
        verify(messageSource, times(2)).getMessage(anyString(), any(), eq(Locale.ENGLISH));
    }

    @Test
    void handleValidationExceptions_NoTranslation() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "name", "Name cannot be blank");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage("Name cannot be blank", null, Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("Name cannot be blank"));

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Name cannot be blank", errors.get("name"));
    }

    @Test
    void handleValidationExceptions_MultipleErrorsSameField() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("object", "name", "Name cannot be blank");
        FieldError fieldError2 = new FieldError("object", "name", "Name too short");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(messageSource.getMessage(anyString(), any(), eq(Locale.ENGLISH)))
            .thenThrow(new NoSuchMessageException(""));

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        // Note: Spring's getAllErrors returns all errors, but typically the last one wins in the map
        assertEquals(1, errors.size()); // Only one entry per field
        assertTrue(errors.containsKey("name"));
    }

    @Test
    void handleValidationExceptions_EmptyErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of());

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    void handleGenericException_WithTranslation() {
        // Given
        Exception ex = new RuntimeException("Some error");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenReturn("Error interno del servidor");

        // When
        ResponseEntity<String> response = handler.handleGenericException(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody());
    }

    @Test
    void handleGenericException_NoTranslation() {
        // Given
        Exception ex = new RuntimeException("Some error");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("error.internal"));

        // When
        ResponseEntity<String> response = handler.handleGenericException(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

    @Test
    void handleGenericException_DifferentLocale() {
        // Given
        Exception ex = new RuntimeException("Some error");
        Locale spanish = Locale.forLanguageTag("es");
        when(messageSource.getMessage("error.internal", null, "Internal server error", spanish))
            .thenReturn("Error interno del servidor");

        // When
        ResponseEntity<String> response = handler.handleGenericException(ex, spanish);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody());
    }
}
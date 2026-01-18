package com.corebanking.audit_service.config;

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

import java.util.HashMap;
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
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage("default message", null, Locale.ENGLISH)).thenReturn("Translated message");

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Translated message", response.getBody().get("field"));
        verify(messageSource).getMessage("default message", null, Locale.ENGLISH);
    }

    @Test
    void handleValidationExceptions_NoTranslation() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage("default message", null, Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("default message"));

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("default message", response.getBody().get("field"));
        verify(messageSource).getMessage("default message", null, Locale.ENGLISH);
    }

    @Test
    void handleValidationExceptions_MultipleFields() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("object", "field1", "message1");
        FieldError fieldError2 = new FieldError("object", "field2", "message2");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(messageSource.getMessage("message1", null, Locale.ENGLISH)).thenReturn("Translated message1");
        when(messageSource.getMessage("message2", null, Locale.ENGLISH)).thenReturn("Translated message2");

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertEquals("Translated message1", body.get("field1"));
        assertEquals("Translated message2", body.get("field2"));
        assertEquals(2, body.size());
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
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void handleGenericException_Success() {
        // Given
        Exception ex = new RuntimeException("Test exception");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenReturn("Internal server error");

        // When
        ResponseEntity<String> response = handler.handleGenericException(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
        verify(messageSource).getMessage("error.internal", null, "Internal server error", Locale.ENGLISH);
    }

    @Test
    void handleGenericException_NoTranslation() {
        // Given
        Exception ex = new RuntimeException("Test exception");
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
        Exception ex = new RuntimeException("Test exception");
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
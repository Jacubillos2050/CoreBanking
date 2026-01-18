package com.corebanking.risk_service.config;

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
        FieldError fieldError1 = new FieldError("object", "loanApplicationId", "Loan application ID cannot be null");
        FieldError fieldError2 = new FieldError("object", "customerCreditScore", "Credit score must be at least 300");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        when(messageSource.getMessage("Loan application ID cannot be null", null, Locale.ENGLISH))
            .thenReturn("El ID de la solicitud de préstamo no puede ser nulo");
        when(messageSource.getMessage("Credit score must be at least 300", null, Locale.ENGLISH))
            .thenReturn("El puntaje crediticio debe ser al menos 300");

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("El ID de la solicitud de préstamo no puede ser nulo", errors.get("loanApplicationId"));
        assertEquals("El puntaje crediticio debe ser al menos 300", errors.get("customerCreditScore"));
        verify(messageSource).getMessage("Loan application ID cannot be null", null, Locale.ENGLISH);
        verify(messageSource).getMessage("Credit score must be at least 300", null, Locale.ENGLISH);
    }

    @Test
    void handleValidationExceptions_WithoutTranslation() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("object", "requestedAmount", "Requested amount must be at least 10000");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError));

        when(messageSource.getMessage("Requested amount must be at least 10000", null, Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("Requested amount must be at least 10000"));

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Requested amount must be at least 10000", errors.get("requestedAmount"));
        verify(messageSource).getMessage("Requested amount must be at least 10000", null, Locale.ENGLISH);
    }

    @Test
    void handleValidationExceptions_MultipleFields() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("object", "termInMonths", "Term must be at least 6 months");
        FieldError fieldError2 = new FieldError("object", "monthlyIncome", "Monthly income must be greater than 0");
        FieldError fieldError3 = new FieldError("object", "customerCreditScore", "Credit score must be at most 850");
        when(ex.getBindingResult().getAllErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenThrow(new NoSuchMessageException("No translation"));

        // When
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals("Term must be at least 6 months", errors.get("termInMonths"));
        assertEquals("Monthly income must be greater than 0", errors.get("monthlyIncome"));
        assertEquals("Credit score must be at most 850", errors.get("customerCreditScore"));
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
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenReturn("Error interno del servidor");

        // When
        ResponseEntity<String> response = handler.handleGenericException(exception, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody());
        verify(messageSource).getMessage("error.internal", null, "Internal server error", Locale.ENGLISH);
    }

    @Test
    void handleGenericException_WithoutTranslation() {
        // Given
        IllegalStateException exception = new IllegalStateException("Unexpected state");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("error.internal"));

        // When
        ResponseEntity<String> response = handler.handleGenericException(exception, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
        verify(messageSource).getMessage("error.internal", null, "Internal server error", Locale.ENGLISH);
    }

    @Test
    void handleGenericException_DifferentLocale() {
        // Given
        Exception exception = new Exception("Some error");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.FRENCH))
            .thenReturn("Erreur interne du serveur");

        // When
        ResponseEntity<String> response = handler.handleGenericException(exception, Locale.FRENCH);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erreur interne du serveur", response.getBody());
        verify(messageSource).getMessage("error.internal", null, "Internal server error", Locale.FRENCH);
    }
}
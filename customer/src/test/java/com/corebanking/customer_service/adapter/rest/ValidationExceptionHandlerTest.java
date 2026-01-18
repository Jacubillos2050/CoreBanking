package com.corebanking.customer_service.adapter.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ValidationExceptionHandler handler;

    @Test
    void handleValidationExceptions_SingleError() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("customer", "name", "Name cannot be blank");
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage(fieldError, Locale.ENGLISH)).thenReturn("El nombre no puede estar vacío");

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("name: El nombre no puede estar vacío; ", errorResponse.error());
    }

    @Test
    void handleValidationExceptions_MultipleErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError1 = new FieldError("customer", "name", "Name cannot be blank");
        FieldError fieldError2 = new FieldError("customer", "email", "Email must be valid");
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(messageSource.getMessage(fieldError1, Locale.ENGLISH)).thenReturn("El nombre no puede estar vacío");
        when(messageSource.getMessage(fieldError2, Locale.ENGLISH)).thenReturn("El email debe ser válido");

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("name: El nombre no puede estar vacío; email: El email debe ser válido; ", errorResponse.error());
    }

    @Test
    void handleValidationExceptions_NoTranslation() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("customer", "name", "Name cannot be blank");
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage(fieldError, Locale.ENGLISH)).thenReturn("Name cannot be blank");

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("name: Name cannot be blank; ", errorResponse.error());
    }

    @Test
    void handleValidationExceptions_EmptyErrors() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of());

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("", errorResponse.error());
    }

    @Test
    void handleValidationExceptions_DifferentLocale() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("customer", "name", "Name cannot be blank");
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));
        Locale spanish = Locale.forLanguageTag("es");
        when(messageSource.getMessage(fieldError, spanish)).thenReturn("El nombre no puede estar vacío");

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, spanish);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("name: El nombre no puede estar vacío; ", errorResponse.error());
    }

    @Test
    void handleValidationExceptions_TrimmedMessage() {
        // Given
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = new FieldError("customer", "name", "Name cannot be blank");
        when(ex.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));
        when(messageSource.getMessage(fieldError, Locale.ENGLISH)).thenReturn("El nombre no puede estar vacío");

        // When
        ResponseEntity<ValidationExceptionHandler.ErrorResponse> response = handler.handleValidationExceptions(ex, Locale.ENGLISH);

        // Then
        ValidationExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        // The message ends with "; " but trim() removes trailing space
        assertTrue(errorResponse.error().endsWith(";"));
    }
}
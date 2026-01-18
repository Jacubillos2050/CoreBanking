package com.corebanking.loan_service.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with translated messages")
    void testHandleValidationExceptionsWithTranslation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));
        when(messageSource.getMessage("default message", null, Locale.ENGLISH)).thenReturn("Translated message");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex, Locale.ENGLISH);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Translated message", response.getBody().get("field"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with default messages when translation fails")
    void testHandleValidationExceptionsWithoutTranslation() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(fieldError));
        when(messageSource.getMessage("default message", null, Locale.ENGLISH))
            .thenThrow(new NoSuchMessageException("default message"));

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex, Locale.ENGLISH);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("default message", response.getBody().get("field"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple errors")
    void testHandleValidationExceptionsMultipleErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        FieldError error1 = new FieldError("object", "field1", "message1");
        FieldError error2 = new FieldError("object", "field2", "message2");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Arrays.asList(error1, error2));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Translated");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex, Locale.ENGLISH);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("field1"));
        assertTrue(errors.containsKey("field2"));
    }

    @Test
    @DisplayName("Should handle generic Exception with translated message")
    void testHandleGenericExceptionWithTranslation() {
        Exception ex = new RuntimeException("test");
        when(messageSource.getMessage("error.internal", null, "Internal server error", Locale.ENGLISH))
            .thenReturn("Translated internal error");

        ResponseEntity<String> response = exceptionHandler.handleGenericException(ex, Locale.ENGLISH);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Translated internal error", response.getBody());
    }

    @Test
    @DisplayName("Should handle generic Exception with default message")
    void testHandleGenericExceptionDefaultMessage() {
        Exception ex = new RuntimeException("test");
        Locale locale = Locale.forLanguageTag("es_CO");
        when(messageSource.getMessage("error.internal", null, "Internal server error", locale))
            .thenReturn("Internal server error");

        ResponseEntity<String> response = exceptionHandler.handleGenericException(ex, locale);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

    @Test
    @DisplayName("Should handle generic Exception with different locales")
    void testHandleGenericExceptionDifferentLocale() {
        Exception ex = new RuntimeException("test");
        Locale spanish = Locale.forLanguageTag("es");
        when(messageSource.getMessage("error.internal", null, "Internal server error", spanish))
            .thenReturn("Error interno del servidor");

        ResponseEntity<String> response = exceptionHandler.handleGenericException(ex, spanish);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor", response.getBody());
    }
}
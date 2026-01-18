package com.corebanking.customer_service.adapter.rest;

import com.corebanking.customer_service.domain.model.Customer;
import com.corebanking.customer_service.domain.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CustomerController controller;

    @Test
    void createCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerService.createCustomer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750)).thenReturn(customer);

        CreateCustomerRequest request = new CreateCustomerRequest(
            "John Doe", "john@example.com", new BigDecimal("5000.00"), 750);

        // When
        ResponseEntity<Object> result = controller.createCustomer(request, "en");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerResponse);
        CustomerResponse response = (CustomerResponse) result.getBody();
        assertEquals(customerId, response.id());
        assertEquals("John Doe", response.name());
        assertEquals("john@example.com", response.email());
        assertEquals(new BigDecimal("5000.00"), response.monthlyIncome());
        assertEquals(750, response.creditScore());
    }

    @Test
    void createCustomer_BusinessValidationFailure() {
        // Given
        when(customerService.createCustomer(anyString(), anyString(), any(BigDecimal.class), anyInt()))
            .thenThrow(new IllegalArgumentException("customer.email.exists"));
        when(messageSource.getMessage("customer.email.exists", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Email already exists");

        CreateCustomerRequest request = new CreateCustomerRequest(
            "John Doe", "john@example.com", new BigDecimal("5000.00"), 750);

        // When
        ResponseEntity<Object> result = controller.createCustomer(request, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("Email already exists", response.error());
    }

    @Test
    void createCustomer_DefaultLocale() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerService.createCustomer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750)).thenReturn(customer);

        CreateCustomerRequest request = new CreateCustomerRequest(
            "John Doe", "john@example.com", new BigDecimal("5000.00"), 750);

        // When
        ResponseEntity<Object> result = controller.createCustomer(request, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getCustomerById_Found() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<Object> result = controller.getCustomerById(customerId, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerResponse);
        CustomerResponse response = (CustomerResponse) result.getBody();
        assertEquals(customerId, response.id());
        assertEquals("John Doe", response.name());
    }

    @Test
    void getCustomerById_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerService.getCustomerById(customerId)).thenReturn(Optional.empty());
        when(messageSource.getMessage("customer.not.found", null, "Customer not found", Locale.ENGLISH))
            .thenReturn("Customer not found");

        // When
        ResponseEntity<Object> result = controller.getCustomerById(customerId, "en");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("Customer not found", response.error());
    }

    @Test
    void getCustomerByEmail_Found() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        // When
        ResponseEntity<Object> result = controller.getCustomerByEmail("john@example.com", "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerResponse);
    }

    @Test
    void getCustomerByEmail_NotFound() {
        // Given
        when(customerService.getCustomerByEmail("john@example.com")).thenReturn(Optional.empty());
        when(messageSource.getMessage("customer.not.found", null, "Customer not found", Locale.ENGLISH))
            .thenReturn("Customer not found");

        // When
        ResponseEntity<Object> result = controller.getCustomerByEmail("john@example.com", "en");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
    }

    @Test
    void updateCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "Jane Doe", "john@example.com",
            new BigDecimal("6000.00"), 800);
        when(customerService.updateCustomer(eq(customerId), eq("Jane Doe"),
            eq(new BigDecimal("6000.00")), eq(800))).thenReturn(customer);

        UpdateCustomerRequest request = new UpdateCustomerRequest(
            "Jane Doe", new BigDecimal("6000.00"), 800);

        // When
        ResponseEntity<Object> result = controller.updateCustomer(customerId, request, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerController.CustomerResponse);
        CustomerController.CustomerResponse response = (CustomerController.CustomerResponse) result.getBody();
        assertEquals("Jane Doe", response.name());
        assertEquals(new BigDecimal("6000.00"), response.monthlyIncome());
        assertEquals(800, response.creditScore());
    }

    @Test
    void updateCustomer_BusinessValidationFailure() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerService.updateCustomer(any(UUID.class), anyString(), any(BigDecimal.class), any()))
            .thenThrow(new IllegalArgumentException("customer.not.found"));
        when(messageSource.getMessage("customer.not.found", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Customer not found");

        CustomerController.UpdateCustomerRequest request = new CustomerController.UpdateCustomerRequest(
            "Jane Doe", new BigDecimal("6000.00"), 800);

        // When
        ResponseEntity<Object> result = controller.updateCustomer(customerId, request, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerController.ErrorResponse);
    }

    @Test
    void updateCustomer_PartialUpdate() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5500.00"), 750); // Only monthlyIncome updated
        when(customerService.updateCustomer(eq(customerId), isNull(), eq(new BigDecimal("5500.00")), isNull()))
            .thenReturn(customer);

        CustomerController.UpdateCustomerRequest request = new CustomerController.UpdateCustomerRequest(
            null, new BigDecimal("5500.00"), null);

        // When
        ResponseEntity<Object> result = controller.updateCustomer(customerId, request, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(messageSource.getMessage("customer.deleted", null, "Customer deleted successfully", Locale.ENGLISH))
            .thenReturn("Customer deleted successfully");

        // When
        ResponseEntity<Object> result = controller.deleteCustomer(customerId, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerController.ErrorResponse);
        CustomerController.ErrorResponse response = (CustomerController.ErrorResponse) result.getBody();
        assertEquals("Customer deleted successfully", response.error());
        verify(customerService).deleteCustomer(customerId);
    }

    @Test
    void deleteCustomer_BusinessValidationFailure() {
        // Given
        UUID customerId = UUID.randomUUID();
        doThrow(new IllegalArgumentException("customer.not.found")).when(customerService).deleteCustomer(customerId);
        when(messageSource.getMessage("customer.not.found", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Customer not found");

        // When
        ResponseEntity<Object> result = controller.deleteCustomer(customerId, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof CustomerController.ErrorResponse);
    }
}
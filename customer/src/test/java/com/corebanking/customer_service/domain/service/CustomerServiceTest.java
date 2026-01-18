package com.corebanking.customer_service.domain.service;

import com.corebanking.customer_service.domain.model.Customer;
import com.corebanking.customer_service.domain.port.CustomerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer savedCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        Customer result = customerService.createCustomer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(new BigDecimal("5000.00"), result.getMonthlyIncome());
        assertEquals(750, result.getCreditScore());
        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_EmailAlreadyExists() {
        // Given
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.createCustomer("John Doe", "john@example.com",
                new BigDecimal("5000.00"), 750));
        assertEquals("customer.email.exists", exception.getMessage());
        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_CreditScoreTooLow() {
        // Given
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.createCustomer("John Doe", "john@example.com",
                new BigDecimal("5000.00"), 250));
        assertEquals("customer.creditScore.invalid", exception.getMessage());
        verify(customerRepository).existsByEmail("john@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_CreditScoreTooHigh() {
        // Given
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.createCustomer("John Doe", "john@example.com",
                new BigDecimal("5000.00"), 900));
        assertEquals("customer.creditScore.invalid", exception.getMessage());
    }

    @Test
    void createCustomer_MonthlyIncomeZero() {
        // Given
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.createCustomer("John Doe", "john@example.com",
                BigDecimal.ZERO, 750));
        assertEquals("customer.monthlyIncome.invalid", exception.getMessage());
    }

    @Test
    void createCustomer_MonthlyIncomeNegative() {
        // Given
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.createCustomer("John Doe", "john@example.com",
                new BigDecimal("-100.00"), 750));
        assertEquals("customer.monthlyIncome.invalid", exception.getMessage());
    }

    @Test
    void createCustomer_CreditScoreMinimum() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer savedCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 300);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        Customer result = customerService.createCustomer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 300);

        // Then
        assertEquals(300, result.getCreditScore());
    }

    @Test
    void createCustomer_CreditScoreMaximum() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer savedCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 850);
        when(customerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        Customer result = customerService.createCustomer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 850);

        // Then
        assertEquals(850, result.getCreditScore());
    }

    @Test
    void getCustomerById_Found() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = customerService.getCustomerById(customerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customerId, result.get().getId());
    }

    @Test
    void getCustomerById_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerService.getCustomerById(customerId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void getCustomerByEmail_Found() {
        // Given
        Customer customer = new Customer(UUID.randomUUID(), "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = customerService.getCustomerByEmail("john@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void getCustomerByEmail_NotFound() {
        // Given
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerService.getCustomerByEmail("john@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void updateCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        Customer updatedCustomer = new Customer(customerId, "Jane Doe", "john@example.com",
            new BigDecimal("6000.00"), 800);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        Customer result = customerService.updateCustomer(customerId, "Jane Doe",
            new BigDecimal("6000.00"), 800);

        // Then
        assertEquals("Jane Doe", result.getName());
        assertEquals(new BigDecimal("6000.00"), result.getMonthlyIncome());
        assertEquals(800, result.getCreditScore());
        assertEquals("john@example.com", result.getEmail()); // Email unchanged
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.updateCustomer(customerId, "Jane Doe",
                new BigDecimal("6000.00"), 800));
        assertEquals("customer.not.found", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_InvalidCreditScore() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.updateCustomer(customerId, "Jane Doe",
                new BigDecimal("6000.00"), 900));
        assertEquals("customer.creditScore.invalid", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_InvalidMonthlyIncome() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.updateCustomer(customerId, "Jane Doe",
                BigDecimal.ZERO, 800));
        assertEquals("customer.monthlyIncome.invalid", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateCustomer_PartialUpdate_NameOnly() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        Customer updatedCustomer = new Customer(customerId, "Jane Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        Customer result = customerService.updateCustomer(customerId, "Jane Doe", null, null);

        // Then
        assertEquals("Jane Doe", result.getName());
        assertEquals(new BigDecimal("5000.00"), result.getMonthlyIncome()); // Unchanged
        assertEquals(750, result.getCreditScore()); // Unchanged
    }

    @Test
    void updateCustomer_PartialUpdate_MonthlyIncomeOnly() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        Customer updatedCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("6000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        Customer result = customerService.updateCustomer(customerId, null, new BigDecimal("6000.00"), null);

        // Then
        assertEquals("John Doe", result.getName()); // Unchanged
        assertEquals(new BigDecimal("6000.00"), result.getMonthlyIncome());
        assertEquals(750, result.getCreditScore()); // Unchanged
    }

    @Test
    void updateCustomer_PartialUpdate_CreditScoreOnly() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        Customer updatedCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 800);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // When
        Customer result = customerService.updateCustomer(customerId, null, null, 800);

        // Then
        assertEquals("John Doe", result.getName()); // Unchanged
        assertEquals(new BigDecimal("5000.00"), result.getMonthlyIncome()); // Unchanged
        assertEquals(800, result.getCreditScore());
    }

    @Test
    void deleteCustomer_Success() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // When
        customerService.deleteCustomer(customerId);

        // Then
        verify(customerRepository).findById(customerId);
        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void deleteCustomer_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerService.deleteCustomer(customerId));
        assertEquals("customer.not.found", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerRepository, never()).deleteById(any(UUID.class));
    }
}
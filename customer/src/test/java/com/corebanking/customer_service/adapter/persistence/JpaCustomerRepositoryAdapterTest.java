package com.corebanking.customer_service.adapter.persistence;

import com.corebanking.customer_service.domain.model.Customer;
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
class JpaCustomerRepositoryAdapterTest {

    @Mock
    private CustomerJpaRepository jpaRepository;

    @InjectMocks
    private JpaCustomerRepositoryAdapter adapter;

    @Test
    void findById_Found() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customerId);
        entity.setName("John Doe");
        entity.setEmail("john@example.com");
        entity.setMonthlyIncome(new BigDecimal("5000.00"));
        entity.setCreditScore(750);
        when(jpaRepository.findById(customerId)).thenReturn(Optional.of(entity));

        // When
        Optional<Customer> result = adapter.findById(customerId);

        // Then
        assertTrue(result.isPresent());
        Customer customer = result.get();
        assertEquals(customerId, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(new BigDecimal("5000.00"), customer.getMonthlyIncome());
        assertEquals(750, customer.getCreditScore());
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        when(jpaRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = adapter.findById(customerId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_Found() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customerId);
        entity.setName("John Doe");
        entity.setEmail("john@example.com");
        entity.setMonthlyIncome(new BigDecimal("5000.00"));
        entity.setCreditScore(750);
        when(jpaRepository.findByEmail("john@example.com")).thenReturn(Optional.of(entity));

        // When
        Optional<Customer> result = adapter.findByEmail("john@example.com");

        // Then
        assertTrue(result.isPresent());
        Customer customer = result.get();
        assertEquals(customerId, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
        assertEquals(new BigDecimal("5000.00"), customer.getMonthlyIncome());
        assertEquals(750, customer.getCreditScore());
    }

    @Test
    void findByEmail_NotFound() {
        // Given
        when(jpaRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = adapter.findByEmail("john@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void save_NewCustomer() {
        // Given
        Customer customer = new Customer("John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        UUID savedId = UUID.randomUUID();
        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(savedId);
        savedEntity.setName("John Doe");
        savedEntity.setEmail("john@example.com");
        savedEntity.setMonthlyIncome(new BigDecimal("5000.00"));
        savedEntity.setCreditScore(750);
        when(jpaRepository.save(any(CustomerEntity.class))).thenReturn(savedEntity);

        // When
        Customer result = adapter.save(customer);

        // Then
        assertEquals(savedId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(new BigDecimal("5000.00"), result.getMonthlyIncome());
        assertEquals(750, result.getCreditScore());
        verify(jpaRepository).save(argThat(entity ->
            entity.getName().equals("John Doe") &&
            entity.getEmail().equals("john@example.com") &&
            entity.getMonthlyIncome().equals(new BigDecimal("5000.00")) &&
            entity.getCreditScore() == 750 &&
            entity.getId() == null
        ));
    }

    @Test
    void save_ExistingCustomer() {
        // Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "John Doe", "john@example.com",
            new BigDecimal("5000.00"), 750);
        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId(customerId);
        savedEntity.setName("John Doe");
        savedEntity.setEmail("john@example.com");
        savedEntity.setMonthlyIncome(new BigDecimal("5000.00"));
        savedEntity.setCreditScore(750);
        when(jpaRepository.save(any(CustomerEntity.class))).thenReturn(savedEntity);

        // When
        Customer result = adapter.save(customer);

        // Then
        assertEquals(customerId, result.getId());
        verify(jpaRepository).save(argThat(entity ->
            entity.getId().equals(customerId) &&
            entity.getName().equals("John Doe") &&
            entity.getEmail().equals("john@example.com") &&
            entity.getMonthlyIncome().equals(new BigDecimal("5000.00")) &&
            entity.getCreditScore() == 750
        ));
    }

    @Test
    void existsByEmail_True() {
        // Given
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When
        boolean result = adapter.existsByEmail("john@example.com");

        // Then
        assertTrue(result);
    }

    @Test
    void existsByEmail_False() {
        // Given
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(false);

        // When
        boolean result = adapter.existsByEmail("john@example.com");

        // Then
        assertFalse(result);
    }

    @Test
    void deleteById() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When
        adapter.deleteById(customerId);

        // Then
        verify(jpaRepository).deleteById(customerId);
    }
}
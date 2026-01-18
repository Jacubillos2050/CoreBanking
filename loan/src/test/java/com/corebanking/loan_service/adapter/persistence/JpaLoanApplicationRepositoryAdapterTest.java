package com.corebanking.loan_service.adapter.persistence;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JpaLoanApplicationRepositoryAdapterTest {

    @Mock
    private LoanApplicationJpaRepository jpaRepository;

    @InjectMocks
    private JpaLoanApplicationRepositoryAdapter adapter;

    private UUID id;
    private UUID customerId;
    private BigDecimal requestedAmount;
    private Integer termInMonths;
    private Instant createdAt;
    private Instant approvedAt;
    private String approvedBy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        customerId = UUID.randomUUID();
        requestedAmount = new BigDecimal("50000");
        termInMonths = 24;
        createdAt = Instant.now();
        approvedAt = Instant.now().plusSeconds(3600);
        approvedBy = "approver@example.com";
    }

    @Test
    @DisplayName("Should find loan by id when exists")
    void testFindByIdExists() {
        LoanApplicationEntity entity = new LoanApplicationEntity(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, approvedAt, approvedBy);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<LoanApplication> result = adapter.findById(id);

        assertTrue(result.isPresent());
        LoanApplication loan = result.get();
        assertEquals(id, loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(requestedAmount, loan.getRequestedAmount());
        assertEquals(termInMonths, loan.getTermInMonths());
        assertEquals(LoanStatus.PENDING, loan.getStatus());
        assertEquals(createdAt, loan.getCreatedAt());
        assertEquals(approvedAt, loan.getApprovedAt());
        assertEquals(approvedBy, loan.getApprovedBy());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Should return empty when loan not found")
    void testFindByIdNotFound() {
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LoanApplication> result = adapter.findById(id);

        assertFalse(result.isPresent());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Should find loans by customer id")
    void testFindByCustomerId() {
        List<LoanApplicationEntity> entities = List.of(
            new LoanApplicationEntity(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, null, null)
        );
        when(jpaRepository.findByCustomerId(customerId)).thenReturn(entities);

        List<LoanApplication> result = adapter.findByCustomerId(customerId);

        assertEquals(1, result.size());
        LoanApplication loan = result.get(0);
        assertEquals(id, loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        verify(jpaRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should find loans by status")
    void testFindByStatus() {
        List<LoanApplicationEntity> entities = List.of(
            new LoanApplicationEntity(id, customerId, requestedAmount, termInMonths, LoanStatus.APPROVED, createdAt, approvedAt, approvedBy)
        );
        when(jpaRepository.findByStatus(LoanStatus.APPROVED)).thenReturn(entities);

        List<LoanApplication> result = adapter.findByStatus(LoanStatus.APPROVED);

        assertEquals(1, result.size());
        LoanApplication loan = result.get(0);
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        verify(jpaRepository).findByStatus(LoanStatus.APPROVED);
    }

    @Test
    @DisplayName("Should save loan application with id")
    void testSaveWithId() {
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, approvedAt, approvedBy);
        LoanApplicationEntity savedEntity = new LoanApplicationEntity(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, approvedAt, approvedBy);
        when(jpaRepository.save(any(LoanApplicationEntity.class))).thenReturn(savedEntity);

        LoanApplication result = adapter.save(loan);

        assertEquals(loan.getId(), result.getId());
        assertEquals(loan.getCustomerId(), result.getCustomerId());
        verify(jpaRepository).save(any(LoanApplicationEntity.class));
    }

    @Test
    @DisplayName("Should save loan application without id")
    void testSaveWithoutId() {
        LoanApplication loan = new LoanApplication(null, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, null, null);
        LoanApplicationEntity savedEntity = new LoanApplicationEntity(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, null, null);
        when(jpaRepository.save(any(LoanApplicationEntity.class))).thenReturn(savedEntity);

        LoanApplication result = adapter.save(loan);

        assertEquals(id, result.getId());
        assertEquals(customerId, result.getCustomerId());
        verify(jpaRepository).save(any(LoanApplicationEntity.class));
    }

}
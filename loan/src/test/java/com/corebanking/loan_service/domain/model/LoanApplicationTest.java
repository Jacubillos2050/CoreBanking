package com.corebanking.loan_service.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationTest {

    private UUID id;
    private UUID customerId;
    private BigDecimal requestedAmount;
    private Integer termInMonths;
    private Instant createdAt;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        customerId = UUID.randomUUID();
        requestedAmount = new BigDecimal("50000");
        termInMonths = 24;
        createdAt = Instant.now();
    }

    @Test
    @DisplayName("Should create loan application with full constructor")
    void testFullConstructor() {
        LoanStatus status = LoanStatus.PENDING;
        Instant approvedAt = null;
        String approvedBy = null;

        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, status, createdAt, approvedAt, approvedBy);

        assertEquals(id, loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(requestedAmount, loan.getRequestedAmount());
        assertEquals(termInMonths, loan.getTermInMonths());
        assertEquals(status, loan.getStatus());
        assertEquals(createdAt, loan.getCreatedAt());
        assertNull(loan.getApprovedAt());
        assertNull(loan.getApprovedBy());
    }

    @Test
    @DisplayName("Should create loan application with creation constructor")
    void testCreationConstructor() {
        LoanApplication loan = new LoanApplication(customerId, requestedAmount, termInMonths);

        assertNull(loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(requestedAmount, loan.getRequestedAmount());
        assertEquals(termInMonths, loan.getTermInMonths());
        assertEquals(LoanStatus.PENDING, loan.getStatus());
        assertNotNull(loan.getCreatedAt());
        assertNull(loan.getApprovedAt());
        assertNull(loan.getApprovedBy());
    }

    @Test
    @DisplayName("Should approve loan when status is PENDING")
    void testApproveWhenPending() {
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, null, null);
        String approvedBy = "approver@example.com";

        loan.approve(approvedBy);

        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertNotNull(loan.getApprovedAt());
        assertEquals(approvedBy, loan.getApprovedBy());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when approving non-pending loan")
    void testApproveWhenNotPending() {
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.APPROVED, createdAt, Instant.now(), "approver");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> loan.approve("another"));
        assertEquals("loan.status.not.pending", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject loan when status is PENDING")
    void testRejectWhenPending() {
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.PENDING, createdAt, null, null);

        loan.reject();

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
        assertNull(loan.getApprovedAt());
        assertNull(loan.getApprovedBy());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when rejecting non-pending loan")
    void testRejectWhenNotPending() {
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.REJECTED, createdAt, null, null);

        IllegalStateException exception = assertThrows(IllegalStateException.class, loan::reject);
        assertEquals("loan.status.not.pending", exception.getMessage());
    }

    @Test
    @DisplayName("Should get all properties correctly")
    void testGetters() {
        Instant approvedAt = Instant.now();
        String approvedBy = "approver";
        LoanApplication loan = new LoanApplication(id, customerId, requestedAmount, termInMonths, LoanStatus.APPROVED, createdAt, approvedAt, approvedBy);

        assertEquals(id, loan.getId());
        assertEquals(customerId, loan.getCustomerId());
        assertEquals(requestedAmount, loan.getRequestedAmount());
        assertEquals(termInMonths, loan.getTermInMonths());
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertEquals(createdAt, loan.getCreatedAt());
        assertEquals(approvedAt, loan.getApprovedAt());
        assertEquals(approvedBy, loan.getApprovedBy());
    }
}
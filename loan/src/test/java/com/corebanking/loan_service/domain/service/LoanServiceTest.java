package com.corebanking.loan_service.domain.service;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import com.corebanking.loan_service.domain.port.LoanApplicationRepositoryPort;
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

class LoanServiceTest {

    @Mock
    private LoanApplicationRepositoryPort loanRepository;

    @InjectMocks
    private LoanService loanService;

    private UUID id;
    private UUID customerId;
    private BigDecimal validAmount;
    private Integer validTerm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        customerId = UUID.randomUUID();
        validAmount = new BigDecimal("50000");
        validTerm = 24;
    }

    @Test
    @DisplayName("Should create loan application successfully")
    void testCreateLoanApplicationSuccess() {
        LoanApplication expected = new LoanApplication(customerId, validAmount, validTerm);
        when(loanRepository.save(any(LoanApplication.class))).thenReturn(expected);

        LoanApplication result = loanService.createLoanApplication(customerId, validAmount, validTerm);

        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(validAmount, result.getRequestedAmount());
        assertEquals(validTerm, result.getTermInMonths());
        assertEquals(LoanStatus.PENDING, result.getStatus());
        verify(loanRepository).save(any(LoanApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for amount too low")
    void testCreateLoanApplicationAmountTooLow() {
        BigDecimal lowAmount = new BigDecimal("5000");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.createLoanApplication(customerId, lowAmount, validTerm));
        assertEquals("loan.amount.too.low", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for amount too high")
    void testCreateLoanApplicationAmountTooHigh() {
        BigDecimal highAmount = new BigDecimal("60000000");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.createLoanApplication(customerId, highAmount, validTerm));
        assertEquals("loan.amount.too.high", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for term too short")
    void testCreateLoanApplicationTermTooShort() {
        Integer shortTerm = 3;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.createLoanApplication(customerId, validAmount, shortTerm));
        assertEquals("loan.term.invalid", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for term too long")
    void testCreateLoanApplicationTermTooLong() {
        Integer longTerm = 70;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.createLoanApplication(customerId, validAmount, longTerm));
        assertEquals("loan.term.invalid", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get loan application by id when exists")
    void testGetLoanApplicationByIdExists() {
        LoanApplication loan = new LoanApplication(id, customerId, validAmount, validTerm, LoanStatus.PENDING, Instant.now(), null, null);
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<LoanApplication> result = loanService.getLoanApplicationById(id);

        assertTrue(result.isPresent());
        assertEquals(loan, result.get());
        verify(loanRepository).findById(id);
    }

    @Test
    @DisplayName("Should return empty optional when loan not found")
    void testGetLoanApplicationByIdNotFound() {
        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        Optional<LoanApplication> result = loanService.getLoanApplicationById(id);

        assertFalse(result.isPresent());
        verify(loanRepository).findById(id);
    }

    @Test
    @DisplayName("Should get loans by customer id")
    void testGetLoanApplicationsByCustomerId() {
        List<LoanApplication> loans = List.of(
            new LoanApplication(UUID.randomUUID(), customerId, validAmount, validTerm, LoanStatus.PENDING, Instant.now(), null, null)
        );
        when(loanRepository.findByCustomerId(customerId)).thenReturn(loans);

        List<LoanApplication> result = loanService.getLoanApplicationsByCustomerId(customerId);

        assertEquals(loans, result);
        verify(loanRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should get loans by status")
    void testGetLoanApplicationsByStatus() {
        List<LoanApplication> loans = List.of(
            new LoanApplication(UUID.randomUUID(), customerId, validAmount, validTerm, LoanStatus.APPROVED, Instant.now(), Instant.now(), "approver")
        );
        when(loanRepository.findByStatus(LoanStatus.APPROVED)).thenReturn(loans);

        List<LoanApplication> result = loanService.getLoanApplicationsByStatus(LoanStatus.APPROVED);

        assertEquals(loans, result);
        verify(loanRepository).findByStatus(LoanStatus.APPROVED);
    }

    @Test
    @DisplayName("Should approve loan application successfully")
    void testApproveLoanApplicationSuccess() {
        LoanApplication loan = mock(LoanApplication.class);
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
        String approvedBy = "approver@example.com";

        LoanApplication result = loanService.approveLoanApplication(id, approvedBy);

        assertEquals(loan, result);
        verify(loan).approve(approvedBy);
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when approving non-existent loan")
    void testApproveLoanApplicationNotFound() {
        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.approveLoanApplication(id, "approver"));
        assertEquals("loan.not.found", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when approving non-pending loan")
    void testApproveLoanApplicationNotPending() {
        LoanApplication loan = mock(LoanApplication.class);
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        doThrow(new IllegalStateException("loan.status.not.pending")).when(loan).approve(anyString());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> loanService.approveLoanApplication(id, "approver"));
        assertEquals("loan.status.not.pending", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject loan application successfully")
    void testRejectLoanApplicationSuccess() {
        LoanApplication loan = mock(LoanApplication.class);
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        LoanApplication result = loanService.rejectLoanApplication(id);

        assertEquals(loan, result);
        verify(loan).reject();
        verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when rejecting non-existent loan")
    void testRejectLoanApplicationNotFound() {
        when(loanRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> loanService.rejectLoanApplication(id));
        assertEquals("loan.not.found", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when rejecting non-pending loan")
    void testRejectLoanApplicationNotPending() {
        LoanApplication loan = mock(LoanApplication.class);
        when(loanRepository.findById(id)).thenReturn(Optional.of(loan));
        doThrow(new IllegalStateException("loan.status.not.pending")).when(loan).reject();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> loanService.rejectLoanApplication(id));
        assertEquals("loan.status.not.pending", exception.getMessage());
        verify(loanRepository, never()).save(any());
    }
}
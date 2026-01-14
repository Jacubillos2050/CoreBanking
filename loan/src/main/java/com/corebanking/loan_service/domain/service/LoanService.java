package com.corebanking.loan_service.domain.service;

import com.corebanking.loan_service.domain.model.LoanApplication;
import com.corebanking.loan_service.domain.model.LoanStatus;
import com.corebanking.loan_service.domain.port.LoanApplicationRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoanService {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("10000");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000000");
    private static final int MIN_TERM_MONTHS = 6;
    private static final int MAX_TERM_MONTHS = 60;

    private final LoanApplicationRepositoryPort loanRepository;

    public LoanService(LoanApplicationRepositoryPort loanRepository) {
        this.loanRepository = loanRepository;
    }

    public LoanApplication createLoanApplication(UUID customerId, BigDecimal requestedAmount, Integer termInMonths) {
        // Validar monto
        if (requestedAmount.compareTo(MIN_AMOUNT) < 0) {
            throw new IllegalArgumentException("loan.amount.too.low");
        }
        if (requestedAmount.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("loan.amount.too.high");
        }

        // Validar plazo
        if (termInMonths < MIN_TERM_MONTHS || termInMonths > MAX_TERM_MONTHS) {
            throw new IllegalArgumentException("loan.term.invalid");
        }

        LoanApplication loanApplication = new LoanApplication(customerId, requestedAmount, termInMonths);
        return loanRepository.save(loanApplication);
    }

    public Optional<LoanApplication> getLoanApplicationById(UUID id) {
        return loanRepository.findById(id);
    }

    public List<LoanApplication> getLoanApplicationsByCustomerId(UUID customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<LoanApplication> getLoanApplicationsByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }

    public LoanApplication approveLoanApplication(UUID id, String approvedBy) {
        LoanApplication loanApplication = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("loan.not.found"));
        
        loanApplication.approve(approvedBy);
        return loanRepository.save(loanApplication);
    }

    public LoanApplication rejectLoanApplication(UUID id) {
        LoanApplication loanApplication = loanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("loan.not.found"));
        
        loanApplication.reject();
        return loanRepository.save(loanApplication);
    }
}


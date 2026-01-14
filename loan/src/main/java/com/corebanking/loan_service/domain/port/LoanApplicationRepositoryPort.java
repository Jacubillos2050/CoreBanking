package com.corebanking.loan_service.domain.port;

import com.corebanking.loan_service.domain.model.LoanApplication;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepositoryPort {
    Optional<LoanApplication> findById(UUID id);
    List<LoanApplication> findByCustomerId(UUID customerId);
    List<LoanApplication> findByStatus(com.corebanking.loan_service.domain.model.LoanStatus status);
    LoanApplication save(LoanApplication loanApplication);
}


package com.corebanking.loan_service.adapter.persistence;

import com.corebanking.loan_service.domain.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationJpaRepository extends JpaRepository<LoanApplicationEntity, UUID> {
    List<LoanApplicationEntity> findByCustomerId(UUID customerId);
    List<LoanApplicationEntity> findByStatus(LoanStatus status);
}


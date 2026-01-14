package com.corebanking.risk_service.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RiskAssessmentJpaRepository extends JpaRepository<RiskAssessmentEntity, UUID> {
    Optional<RiskAssessmentEntity> findByLoanApplicationId(UUID loanApplicationId);
}


package com.corebanking.risk_service.domain.port;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import java.util.Optional;
import java.util.UUID;

public interface RiskAssessmentRepositoryPort {
    Optional<RiskAssessment> findByLoanApplicationId(UUID loanApplicationId);
    RiskAssessment save(RiskAssessment riskAssessment);
    Optional<RiskAssessment> findById(UUID id);
}


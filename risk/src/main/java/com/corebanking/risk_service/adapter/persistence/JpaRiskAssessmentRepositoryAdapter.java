package com.corebanking.risk_service.adapter.persistence;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import com.corebanking.risk_service.domain.port.RiskAssessmentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRiskAssessmentRepositoryAdapter implements RiskAssessmentRepositoryPort {

    private final RiskAssessmentJpaRepository jpaRepository;

    public JpaRiskAssessmentRepositoryAdapter(RiskAssessmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<RiskAssessment> findByLoanApplicationId(UUID loanApplicationId) {
        return jpaRepository.findByLoanApplicationId(loanApplicationId)
                .map(this::toDomain);
    }

    @Override
    public RiskAssessment save(RiskAssessment riskAssessment) {
        RiskAssessmentEntity entity = toEntity(riskAssessment);
        RiskAssessmentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RiskAssessment> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    private RiskAssessment toDomain(RiskAssessmentEntity entity) {
        return new RiskAssessment(
            entity.getId(),
            entity.getLoanApplicationId(),
            entity.getRiskScore(),
            entity.getRiskLevel(),
            entity.getRulesApplied(),
            entity.getEvaluatedAt()
        );
    }

    private RiskAssessmentEntity toEntity(RiskAssessment riskAssessment) {
        RiskAssessmentEntity entity = new RiskAssessmentEntity();
        if (riskAssessment.getId() != null) {
            entity.setId(riskAssessment.getId());
        }
        entity.setLoanApplicationId(riskAssessment.getLoanApplicationId());
        entity.setRiskScore(riskAssessment.getRiskScore());
        entity.setRiskLevel(riskAssessment.getRiskLevel());
        entity.setRulesApplied(riskAssessment.getRulesApplied());
        entity.setEvaluatedAt(riskAssessment.getEvaluatedAt());
        return entity;
    }
}


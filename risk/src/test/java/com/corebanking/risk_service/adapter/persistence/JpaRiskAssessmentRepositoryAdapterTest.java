package com.corebanking.risk_service.adapter.persistence;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import com.corebanking.risk_service.domain.model.RiskLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaRiskAssessmentRepositoryAdapterTest {

    @Mock
    private RiskAssessmentJpaRepository jpaRepository;

    @InjectMocks
    private JpaRiskAssessmentRepositoryAdapter adapter;

    @Test
    void findByLoanApplicationId_Found() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        Instant evaluatedAt = Instant.now();
        RiskAssessmentEntity entity = new RiskAssessmentEntity();
        entity.setId(assessmentId);
        entity.setLoanApplicationId(loanApplicationId);
        entity.setRiskScore(45);
        entity.setRiskLevel(RiskLevel.MEDIUM);
        entity.setRulesApplied(List.of("CREDIT_SCORE_BELOW_700", "DEBT_TO_INCOME_MEDIUM"));
        entity.setEvaluatedAt(evaluatedAt);

        when(jpaRepository.findByLoanApplicationId(loanApplicationId))
            .thenReturn(Optional.of(entity));

        // When
        Optional<RiskAssessment> result = adapter.findByLoanApplicationId(loanApplicationId);

        // Then
        assertTrue(result.isPresent());
        RiskAssessment assessment = result.get();
        assertEquals(assessmentId, assessment.getId());
        assertEquals(loanApplicationId, assessment.getLoanApplicationId());
        assertEquals(45, assessment.getRiskScore());
        assertEquals(RiskLevel.MEDIUM, assessment.getRiskLevel());
        assertEquals(2, assessment.getRulesApplied().size());
        assertEquals(evaluatedAt, assessment.getEvaluatedAt());
        verify(jpaRepository).findByLoanApplicationId(loanApplicationId);
    }

    @Test
    void findByLoanApplicationId_NotFound() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();

        when(jpaRepository.findByLoanApplicationId(loanApplicationId))
            .thenReturn(Optional.empty());

        // When
        Optional<RiskAssessment> result = adapter.findByLoanApplicationId(loanApplicationId);

        // Then
        assertFalse(result.isPresent());
        verify(jpaRepository).findByLoanApplicationId(loanApplicationId);
    }

    @Test
    void save_NewAssessment() {
        // Given
        UUID loanApplicationId = UUID.randomUUID();
        UUID savedId = UUID.randomUUID();
        Instant evaluatedAt = Instant.now();
        RiskAssessment assessment = new RiskAssessment(
            loanApplicationId,
            25,
            RiskLevel.LOW,
            List.of("CREDIT_SCORE_ACCEPTABLE"),
            evaluatedAt
        );

        RiskAssessmentEntity savedEntity = new RiskAssessmentEntity();
        savedEntity.setId(savedId);
        savedEntity.setLoanApplicationId(loanApplicationId);
        savedEntity.setRiskScore(25);
        savedEntity.setRiskLevel(RiskLevel.LOW);
        savedEntity.setRulesApplied(List.of("CREDIT_SCORE_ACCEPTABLE"));
        savedEntity.setEvaluatedAt(evaluatedAt);

        when(jpaRepository.save(any(RiskAssessmentEntity.class))).thenReturn(savedEntity);

        // When
        RiskAssessment result = adapter.save(assessment);

        // Then
        assertNotNull(result);
        assertEquals(savedId, result.getId());
        assertEquals(loanApplicationId, result.getLoanApplicationId());
        assertEquals(25, result.getRiskScore());
        assertEquals(RiskLevel.LOW, result.getRiskLevel());
        assertEquals(1, result.getRulesApplied().size());
        assertEquals(evaluatedAt, result.getEvaluatedAt());
        verify(jpaRepository).save(any(RiskAssessmentEntity.class));
    }

    @Test
    void save_ExistingAssessment() {
        // Given
        UUID assessmentId = UUID.randomUUID();
        UUID loanApplicationId = UUID.randomUUID();
        Instant evaluatedAt = Instant.now();
        RiskAssessment assessment = new RiskAssessment(
            assessmentId,
            loanApplicationId,
            75,
            RiskLevel.HIGH,
            List.of("CREDIT_SCORE_BELOW_600"),
            evaluatedAt
        );

        RiskAssessmentEntity savedEntity = new RiskAssessmentEntity();
        savedEntity.setId(assessmentId);
        savedEntity.setLoanApplicationId(loanApplicationId);
        savedEntity.setRiskScore(75);
        savedEntity.setRiskLevel(RiskLevel.HIGH);
        savedEntity.setRulesApplied(List.of("CREDIT_SCORE_BELOW_600"));
        savedEntity.setEvaluatedAt(evaluatedAt);

        when(jpaRepository.save(any(RiskAssessmentEntity.class))).thenReturn(savedEntity);

        // When
        RiskAssessment result = adapter.save(assessment);

        // Then
        assertNotNull(result);
        assertEquals(assessmentId, result.getId());
        assertEquals(loanApplicationId, result.getLoanApplicationId());
        assertEquals(75, result.getRiskScore());
        assertEquals(RiskLevel.HIGH, result.getRiskLevel());
        assertEquals(1, result.getRulesApplied().size());
        assertEquals(evaluatedAt, result.getEvaluatedAt());
        verify(jpaRepository).save(any(RiskAssessmentEntity.class));
    }

    @Test
    void findById_Found() {
        // Given
        UUID assessmentId = UUID.randomUUID();
        UUID loanApplicationId = UUID.randomUUID();
        Instant evaluatedAt = Instant.now();
        RiskAssessmentEntity entity = new RiskAssessmentEntity();
        entity.setId(assessmentId);
        entity.setLoanApplicationId(loanApplicationId);
        entity.setRiskScore(50);
        entity.setRiskLevel(RiskLevel.MEDIUM);
        entity.setRulesApplied(List.of("TERM_LONG", "AMOUNT_HIGH"));
        entity.setEvaluatedAt(evaluatedAt);

        when(jpaRepository.findById(assessmentId))
            .thenReturn(Optional.of(entity));

        // When
        Optional<RiskAssessment> result = adapter.findById(assessmentId);

        // Then
        assertTrue(result.isPresent());
        RiskAssessment assessment = result.get();
        assertEquals(assessmentId, assessment.getId());
        assertEquals(loanApplicationId, assessment.getLoanApplicationId());
        assertEquals(50, assessment.getRiskScore());
        assertEquals(RiskLevel.MEDIUM, assessment.getRiskLevel());
        assertEquals(2, assessment.getRulesApplied().size());
        assertEquals(evaluatedAt, assessment.getEvaluatedAt());
        verify(jpaRepository).findById(assessmentId);
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID assessmentId = UUID.randomUUID();

        when(jpaRepository.findById(assessmentId))
            .thenReturn(Optional.empty());

        // When
        Optional<RiskAssessment> result = adapter.findById(assessmentId);

        // Then
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(assessmentId);
    }
}
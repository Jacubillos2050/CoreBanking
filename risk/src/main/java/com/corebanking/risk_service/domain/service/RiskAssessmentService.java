package com.corebanking.risk_service.domain.service;

import com.corebanking.risk_service.domain.model.RiskAssessment;
import com.corebanking.risk_service.domain.model.RiskLevel;
import com.corebanking.risk_service.domain.port.RiskAssessmentRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RiskAssessmentService {

    private final RiskAssessmentRepositoryPort riskAssessmentRepository;

    public RiskAssessmentService(RiskAssessmentRepositoryPort riskAssessmentRepository) {
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    /**
     * Evalúa el riesgo de una solicitud de préstamo basándose en múltiples factores.
     * 
     * @param loanApplicationId ID de la solicitud de préstamo
     * @param customerCreditScore Puntaje crediticio del cliente (300-850)
     * @param requestedAmount Monto solicitado
     * @param termInMonths Plazo en meses (6-60)
     * @param monthlyIncome Ingresos mensuales del cliente
     * @return RiskAssessment con el análisis de riesgo
     */
    public RiskAssessment evaluateRisk(UUID loanApplicationId, Integer customerCreditScore,
                                      BigDecimal requestedAmount, Integer termInMonths,
                                      BigDecimal monthlyIncome) {
        
        List<String> rulesApplied = new ArrayList<>();
        int riskScore = 0;

        // Regla 1: Credit Score
        if (customerCreditScore < 600) {
            riskScore += 40;
            rulesApplied.add("CREDIT_SCORE_BELOW_600");
        } else if (customerCreditScore < 650) {
            riskScore += 25;
            rulesApplied.add("CREDIT_SCORE_BELOW_650");
        } else if (customerCreditScore < 700) {
            riskScore += 15;
            rulesApplied.add("CREDIT_SCORE_BELOW_700");
        } else {
            rulesApplied.add("CREDIT_SCORE_ACCEPTABLE");
        }

        // Regla 2: Relación monto solicitado vs ingresos anuales
        BigDecimal annualIncome = monthlyIncome.multiply(BigDecimal.valueOf(12));
        BigDecimal debtToIncomeRatio = requestedAmount.divide(annualIncome, 4, java.math.RoundingMode.HALF_UP);
        
        if (debtToIncomeRatio.compareTo(BigDecimal.valueOf(0.5)) > 0) {
            riskScore += 30;
            rulesApplied.add("DEBT_TO_INCOME_HIGH");
        } else if (debtToIncomeRatio.compareTo(BigDecimal.valueOf(0.3)) > 0) {
            riskScore += 15;
            rulesApplied.add("DEBT_TO_INCOME_MEDIUM");
        } else {
            rulesApplied.add("DEBT_TO_INCOME_ACCEPTABLE");
        }

        // Regla 3: Plazo del préstamo
        if (termInMonths > 48) {
            riskScore += 15;
            rulesApplied.add("TERM_TOO_LONG");
        } else if (termInMonths > 36) {
            riskScore += 10;
            rulesApplied.add("TERM_LONG");
        } else {
            rulesApplied.add("TERM_ACCEPTABLE");
        }

        // Regla 4: Ingresos muy bajos
        if (monthlyIncome.compareTo(BigDecimal.valueOf(1000)) < 0) {
            riskScore += 20;
            rulesApplied.add("INCOME_TOO_LOW");
        } else if (monthlyIncome.compareTo(BigDecimal.valueOf(2000)) < 0) {
            riskScore += 10;
            rulesApplied.add("INCOME_LOW");
        } else {
            rulesApplied.add("INCOME_ACCEPTABLE");
        }

        // Regla 5: Monto solicitado muy alto
        if (requestedAmount.compareTo(BigDecimal.valueOf(1000000)) > 0) {
            riskScore += 15;
            rulesApplied.add("AMOUNT_VERY_HIGH");
        } else if (requestedAmount.compareTo(BigDecimal.valueOf(500000)) > 0) {
            riskScore += 10;
            rulesApplied.add("AMOUNT_HIGH");
        } else {
            rulesApplied.add("AMOUNT_ACCEPTABLE");
        }

        // Asegurar que el riskScore esté en el rango 0-100
        riskScore = Math.min(100, Math.max(0, riskScore));

        // Determinar el nivel de riesgo
        RiskLevel riskLevel;
        if (riskScore > 70) {
            riskLevel = RiskLevel.HIGH;
        } else if (riskScore > 40) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.LOW;
        }

        RiskAssessment assessment = new RiskAssessment(
            loanApplicationId,
            riskScore,
            riskLevel,
            rulesApplied,
            Instant.now()
        );

        return riskAssessmentRepository.save(assessment);
    }

    public Optional<RiskAssessment> getByLoanApplicationId(UUID loanApplicationId) {
        return riskAssessmentRepository.findByLoanApplicationId(loanApplicationId);
    }

    public Optional<RiskAssessment> getById(UUID id) {
        return riskAssessmentRepository.findById(id);
    }
}


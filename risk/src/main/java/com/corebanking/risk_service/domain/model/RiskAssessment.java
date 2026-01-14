package com.corebanking.risk_service.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RiskAssessment {
    private UUID id;
    private UUID loanApplicationId;
    private Integer riskScore; // 0-100, where >70 = high risk
    private RiskLevel riskLevel;
    private List<String> rulesApplied;
    private Instant evaluatedAt;

    public RiskAssessment(UUID id, UUID loanApplicationId, Integer riskScore, 
                         RiskLevel riskLevel, List<String> rulesApplied, Instant evaluatedAt) {
        this.id = id;
        this.loanApplicationId = loanApplicationId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.rulesApplied = rulesApplied;
        this.evaluatedAt = evaluatedAt;
    }

    public RiskAssessment(UUID loanApplicationId, Integer riskScore, 
                         RiskLevel riskLevel, List<String> rulesApplied, Instant evaluatedAt) {
        this(null, loanApplicationId, riskScore, riskLevel, rulesApplied, evaluatedAt);
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getLoanApplicationId() { return loanApplicationId; }
    public Integer getRiskScore() { return riskScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getRulesApplied() { return rulesApplied; }
    public Instant getEvaluatedAt() { return evaluatedAt; }
}


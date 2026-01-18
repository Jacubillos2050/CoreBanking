package com.corebanking.risk_service.adapter.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RiskAssessmentResponse(
        UUID id,
        UUID loanApplicationId,
        Integer riskScore,
        String riskLevel,
        List<String> rulesApplied,
        Instant evaluatedAt
) {}
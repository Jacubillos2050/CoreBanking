package com.corebanking.risk_service.adapter.rest;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record EvaluateRiskRequest(
        @NotNull(message = "Loan application ID cannot be null")
        UUID loanApplicationId,

        @NotNull(message = "Customer credit score cannot be null")
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        Integer customerCreditScore,

        @NotNull(message = "Requested amount cannot be null")
        @DecimalMin(value = "10000", message = "Requested amount must be at least 10000")
        @DecimalMax(value = "50000000", message = "Requested amount must be at most 50000000")
        BigDecimal requestedAmount,

        @NotNull(message = "Term in months cannot be null")
        @Min(value = 6, message = "Term must be at least 6 months")
        @Max(value = 60, message = "Term must be at most 60 months")
        Integer termInMonths,

        @NotNull(message = "Monthly income cannot be null")
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome
) {}
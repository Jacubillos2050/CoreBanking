package com.corebanking.loan_service.adapter.rest;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateLoanApplicationRequest(
        @NotNull(message = "Customer ID cannot be null")
        UUID customerId,

        @NotNull(message = "Requested amount cannot be null")
        @DecimalMin(value = "10000", message = "Requested amount must be at least 10000")
        @DecimalMax(value = "50000000", message = "Requested amount must be at most 50000000")
        BigDecimal requestedAmount,

        @NotNull(message = "Term in months cannot be null")
        @Min(value = 6, message = "Term in months must be at least 6")
        @Max(value = 60, message = "Term in months must be at most 60")
        Integer termInMonths
) {}
package com.corebanking.loan_service.adapter.rest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanApplicationResponse(
        UUID id,
        UUID customerId,
        BigDecimal requestedAmount,
        Integer termInMonths,
        String status,
        Instant createdAt,
        Instant approvedAt,
        String approvedBy
) {}
package com.corebanking.customer_service.adapter.rest;

import java.math.BigDecimal;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        BigDecimal monthlyIncome,
        Integer creditScore
) {}
package com.corebanking.customer_service.adapter.rest;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateCustomerRequest(
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,

        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        Integer creditScore
) {}
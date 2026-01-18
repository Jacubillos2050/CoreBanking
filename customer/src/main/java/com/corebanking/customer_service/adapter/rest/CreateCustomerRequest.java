package com.corebanking.customer_service.adapter.rest;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateCustomerRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotNull(message = "Monthly income cannot be null")
        @DecimalMin(value = "0.01", message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,

        @NotNull(message = "Credit score cannot be null")
        @Min(value = 300, message = "Credit score must be at least 300")
        @Max(value = 850, message = "Credit score must be at most 850")
        Integer creditScore
) {}
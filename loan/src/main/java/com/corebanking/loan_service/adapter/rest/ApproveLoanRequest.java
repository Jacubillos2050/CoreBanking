package com.corebanking.loan_service.adapter.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApproveLoanRequest(
        @NotBlank(message = "Approved by cannot be blank")
        @Size(max = 100, message = "Approved by must not exceed 100 characters")
        String approvedBy
) {}
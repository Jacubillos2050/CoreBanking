package com.corebanking.audit_service.adapter.rest;

import jakarta.validation.constraints.NotBlank;

public record CreateAuditLogRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,

        @NotBlank(message = "Action cannot be blank")
        String action,

        @NotBlank(message = "Entity type cannot be blank")
        String entityType,

        String entityId,
        String details
) {}
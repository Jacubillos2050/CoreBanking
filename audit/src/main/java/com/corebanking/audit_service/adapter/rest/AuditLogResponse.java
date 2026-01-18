package com.corebanking.audit_service.adapter.rest;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String userId,
        String action,
        String entityType,
        String entityId,
        String details,
        LocalDateTime timestamp,
        String ipAddress,
        String userAgent
) {}
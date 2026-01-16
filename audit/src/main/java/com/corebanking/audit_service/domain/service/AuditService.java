package com.corebanking.audit_service.domain.service;

import com.corebanking.audit_service.domain.model.AuditLog;
import com.corebanking.audit_service.domain.port.AuditRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditRepositoryPort auditRepository;

    public AuditService(AuditRepositoryPort auditRepository) {
        this.auditRepository = auditRepository;
    }

    public AuditLog createAuditLog(String userId, String action, String entityType, 
                                  String entityId, String details, String ipAddress, String userAgent) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("audit.userId.required");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("audit.action.required");
        }
        if (entityType == null || entityType.isBlank()) {
            throw new IllegalArgumentException("audit.entityType.required");
        }
        
        AuditLog auditLog = new AuditLog(
            userId,
            action,
            entityType,
            entityId,
            details,
            LocalDateTime.now(),
            ipAddress,
            userAgent
        );
        
        return auditRepository.save(auditLog);
    }

    public Optional<AuditLog> getAuditLogById(UUID id) {
        return auditRepository.findById(id);
    }

    public List<AuditLog> getAuditLogsByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("audit.userId.required");
        }
        return auditRepository.findByUserId(userId);
    }

    public List<AuditLog> getAuditLogsByEntityType(String entityType) {
        if (entityType == null || entityType.isBlank()) {
            throw new IllegalArgumentException("audit.entityType.required");
        }
        return auditRepository.findByEntityType(entityType);
    }

    public List<AuditLog> getAuditLogsByEntity(String entityType, String entityId) {
        if (entityType == null || entityType.isBlank()) {
            throw new IllegalArgumentException("audit.entityType.required");
        }
        if (entityId == null || entityId.isBlank()) {
            throw new IllegalArgumentException("audit.entityId.required");
        }
        return auditRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAuditLogsByAction(String action) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("audit.action.required");
        }
        return auditRepository.findByAction(action);
    }

    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("audit.dateRange.required");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("audit.dateRange.invalid");
        }
        return auditRepository.findByTimestampBetween(start, end);
    }

    public List<AuditLog> getAuditLogsByUserAndDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("audit.userId.required");
        }
        if (start == null || end == null) {
            throw new IllegalArgumentException("audit.dateRange.required");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("audit.dateRange.invalid");
        }
        return auditRepository.findByUserIdAndTimestampBetween(userId, start, end);
    }
}


package com.corebanking.audit_service.domain.port;

import com.corebanking.audit_service.domain.model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditRepositoryPort {
    AuditLog save(AuditLog auditLog);
    Optional<AuditLog> findById(UUID id);
    List<AuditLog> findByUserId(String userId);
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<AuditLog> findByAction(String action);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);
}


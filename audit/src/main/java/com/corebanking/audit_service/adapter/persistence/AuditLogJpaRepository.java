package com.corebanking.audit_service.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, UUID> {
    List<AuditLogEntity> findByUserId(String userId);
    List<AuditLogEntity> findByEntityType(String entityType);
    List<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, String entityId);
    List<AuditLogEntity> findByAction(String action);
    List<AuditLogEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLogEntity> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);
}


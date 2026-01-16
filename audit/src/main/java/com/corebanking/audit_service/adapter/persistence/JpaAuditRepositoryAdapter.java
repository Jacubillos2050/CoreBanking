package com.corebanking.audit_service.adapter.persistence;

import com.corebanking.audit_service.domain.model.AuditLog;
import com.corebanking.audit_service.domain.port.AuditRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaAuditRepositoryAdapter implements AuditRepositoryPort {

    private final AuditLogJpaRepository jpaRepository;

    public JpaAuditRepositoryAdapter(AuditLogJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AuditLog> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<AuditLog> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByEntityType(String entityType) {
        return jpaRepository.findByEntityType(entityType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId) {
        return jpaRepository.findByEntityTypeAndEntityId(entityType, entityId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByAction(String action) {
        return jpaRepository.findByAction(action).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByTimestampBetween(start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByUserIdAndTimestampBetween(userId, start, end).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
            entity.getId(),
            entity.getUserId(),
            entity.getAction(),
            entity.getEntityType(),
            entity.getEntityId(),
            entity.getDetails(),
            entity.getTimestamp(),
            entity.getIpAddress(),
            entity.getUserAgent()
        );
    }

    private AuditLogEntity toEntity(AuditLog auditLog) {
        AuditLogEntity entity = new AuditLogEntity();
        if (auditLog.getId() != null) {
            entity.setId(auditLog.getId());
        }
        entity.setUserId(auditLog.getUserId());
        entity.setAction(auditLog.getAction());
        entity.setEntityType(auditLog.getEntityType());
        entity.setEntityId(auditLog.getEntityId());
        entity.setDetails(auditLog.getDetails());
        entity.setTimestamp(auditLog.getTimestamp());
        entity.setIpAddress(auditLog.getIpAddress());
        entity.setUserAgent(auditLog.getUserAgent());
        return entity;
    }
}


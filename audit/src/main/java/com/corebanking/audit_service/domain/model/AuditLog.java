package com.corebanking.audit_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {
    private UUID id;
    private String userId;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;
    private String userAgent;

    public AuditLog(UUID id, String userId, String action, String entityType, String entityId, 
                   String details, LocalDateTime timestamp, String ipAddress, String userAgent) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public AuditLog(String userId, String action, String entityType, String entityId, 
                   String details, LocalDateTime timestamp, String ipAddress, String userAgent) {
        this(null, userId, action, entityType, entityId, details, timestamp, ipAddress, userAgent);
    }

    // Getters
    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
}


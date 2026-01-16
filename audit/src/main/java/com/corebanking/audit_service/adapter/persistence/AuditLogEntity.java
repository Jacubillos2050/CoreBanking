package com.corebanking.audit_service.adapter.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_entity_type", columnList = "entity_type"),
    @Index(name = "idx_entity_id", columnList = "entity_id"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_entity_type_id", columnList = "entity_type, entity_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, length = 100)
    @NotBlank(message = "User ID cannot be blank")
    @NotNull(message = "User ID cannot be null")
    private String userId;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Action cannot be blank")
    @NotNull(message = "Action cannot be null")
    private String action;
    
    @Column(name = "entity_type", nullable = false, length = 100)
    @NotBlank(message = "Entity type cannot be blank")
    @NotNull(message = "Entity type cannot be null")
    private String entityType;
    
    @Column(name = "entity_id", length = 100)
    private String entityId;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false)
    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
}


package com.corebanking.audit_service.adapter.rest;

import com.corebanking.audit_service.domain.model.AuditLog;
import com.corebanking.audit_service.domain.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    private final AuditService auditService;
    private final MessageSource messageSource;

    public AuditController(AuditService auditService, MessageSource messageSource) {
        this.auditService = auditService;
        this.messageSource = messageSource;
    }

    @PostMapping
    public ResponseEntity<Object> createAuditLog(
            @Valid @RequestBody CreateAuditLogRequest request,
            HttpServletRequest httpRequest,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        log.info("Received create audit log request: userId={}, action={}, entityType={}, entityId={}",
                request.userId(), request.action(), request.entityType(), request.entityId());

        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            AuditLog auditLog = auditService.createAuditLog(
                request.userId(),
                request.action(),
                request.entityType(),
                request.entityId(),
                request.details(),
                ipAddress,
                userAgent
            );

            AuditLogResponse response = new AuditLogResponse(
                auditLog.getId(),
                auditLog.getUserId(),
                auditLog.getAction(),
                auditLog.getEntityType(),
                auditLog.getEntityId(),
                auditLog.getDetails(),
                auditLog.getTimestamp(),
                auditLog.getIpAddress(),
                auditLog.getUserAgent()
            );

            log.info("Audit log created successfully with ID: {}", auditLog.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Business validation failed: {}", e.getMessage());
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAuditLogById(
            @PathVariable UUID id,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        return auditService.getAuditLogById(id)
                .map(auditLog -> ResponseEntity.<Object>ok(new AuditLogResponse(
                    auditLog.getId(),
                    auditLog.getUserId(),
                    auditLog.getAction(),
                    auditLog.getEntityType(),
                    auditLog.getEntityId(),
                    auditLog.getDetails(),
                    auditLog.getTimestamp(),
                    auditLog.getIpAddress(),
                    auditLog.getUserAgent()
                )))
                .orElseGet(() -> {
                    String message = messageSource.getMessage("audit.not.found", null, "Audit log not found", locale);
                    return ResponseEntity.<Object>status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse(message));
                });
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getAuditLogsByUserId(
            @PathVariable String userId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByUserId(userId);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<Object> getAuditLogsByEntityType(
            @PathVariable String entityType,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByEntityType(entityType);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Object> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByEntity(entityType, entityId);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<Object> getAuditLogsByAction(
            @PathVariable String action,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByAction(action);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<Object> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByDateRange(start, end);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<Object> getAuditLogsByUserAndDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsByUserAndDateRange(userId, start, end);
            List<AuditLogResponse> responses = auditLogs.stream()
                    .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getTimestamp(),
                        log.getIpAddress(),
                        log.getUserAgent()
                    ))
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

// DTOs
record CreateAuditLogRequest(
        @NotBlank(message = "User ID cannot be blank")
        String userId,
        
        @NotBlank(message = "Action cannot be blank")
        String action,
        
        @NotBlank(message = "Entity type cannot be blank")
        String entityType,
        
        String entityId,
        String details
) {}

record AuditLogResponse(
        java.util.UUID id,
        String userId,
        String action,
        String entityType,
        String entityId,
        String details,
        java.time.LocalDateTime timestamp,
        String ipAddress,
        String userAgent
) {}

record ErrorResponse(String error) {}


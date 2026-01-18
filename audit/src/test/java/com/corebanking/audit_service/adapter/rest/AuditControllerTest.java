package com.corebanking.audit_service.adapter.rest;

import com.corebanking.audit_service.domain.model.AuditLog;
import com.corebanking.audit_service.domain.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private AuditController controller;

    @Test
    void createAuditLog_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(auditLog);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "user123", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, "en");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody() instanceof AuditLogResponse);
        AuditLogResponse response = (AuditLogResponse) result.getBody();
        assertEquals(auditLog.getId(), response.id());
        assertEquals("user123", response.userId());
        assertEquals("CREATE", response.action());
        assertEquals("USER", response.entityType());
        assertEquals("user456", response.entityId());
        assertEquals("Created user", response.details());
        assertEquals("192.168.1.1", response.ipAddress());
        assertEquals("Mozilla/5.0", response.userAgent());
    }

    @Test
    void createAuditLog_WithXForwardedFor() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "10.0.0.1", "Mozilla/5.0"
        );
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(auditLog);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 192.168.1.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "user123", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, "en");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        AuditLogResponse response = (AuditLogResponse) result.getBody();
        assertEquals("10.0.0.1", response.ipAddress());
    }

    @Test
    void createAuditLog_WithXRealIP() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "10.0.0.1", "Mozilla/5.0"
        );
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(auditLog);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getHeader("X-Real-IP")).thenReturn("10.0.0.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "user123", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, "en");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        AuditLogResponse response = (AuditLogResponse) result.getBody();
        assertEquals("10.0.0.1", response.ipAddress());
    }

    @Test
    void createAuditLog_ValidationError() {
        // Given
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("audit.userId.required"));
        when(messageSource.getMessage("audit.userId.required", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("User ID is required");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("User ID is required", response.error());
    }

    @Test
    void createAuditLog_WithAcceptLanguageSpanish() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(auditLog);
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "user123", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, "es");

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void createAuditLog_NullAcceptLanguage() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.createAuditLog(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(auditLog);
        when(httpRequest.getRemoteAddr()).thenReturn("192.168.1.1");
        when(httpRequest.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        CreateAuditLogRequest request = new CreateAuditLogRequest(
            "user123", "CREATE", "USER", "user456", "Created user"
        );

        // When
        ResponseEntity<Object> result = controller.createAuditLog(request, httpRequest, null);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getAuditLogById_Found() {
        // Given
        UUID id = UUID.randomUUID();
        AuditLog auditLog = new AuditLog(
            id, "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogById(id)).thenReturn(Optional.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogById(id, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof AuditLogResponse);
        AuditLogResponse response = (AuditLogResponse) result.getBody();
        assertEquals(id, response.id());
    }

    @Test
    void getAuditLogById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(auditService.getAuditLogById(id)).thenReturn(Optional.empty());
        when(messageSource.getMessage("audit.not.found", null, "Audit log not found", Locale.ENGLISH))
            .thenReturn("Audit log not found");

        // When
        ResponseEntity<Object> result = controller.getAuditLogById(id, "en");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("Audit log not found", response.error());
    }

    @Test
    void getAuditLogsByUserId_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByUserId("user123")).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByUserId("user123", "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
        assertEquals("user123", responses.get(0).userId());
    }

    @Test
    void getAuditLogsByUserId_ValidationError() {
        // Given
        when(auditService.getAuditLogsByUserId("")).thenThrow(new IllegalArgumentException("audit.userId.required"));
        when(messageSource.getMessage("audit.userId.required", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("User ID is required");

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByUserId("", "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("User ID is required", response.error());
    }

    @Test
    void getAuditLogsByEntityType_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByEntityType("USER")).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByEntityType("USER", "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
        assertEquals("USER", responses.get(0).entityType());
    }

    @Test
    void getAuditLogsByEntity_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByEntity("USER", "user456")).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByEntity("USER", "user456", "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
        assertEquals("user456", responses.get(0).entityId());
    }

    @Test
    void getAuditLogsByEntity_ValidationError() {
        // Given
        when(auditService.getAuditLogsByEntity("", "user456")).thenThrow(new IllegalArgumentException("audit.entityType.required"));
        when(messageSource.getMessage("audit.entityType.required", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Entity type is required");

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByEntity("", "user456", "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("Entity type is required", response.error());
    }

    @Test
    void getAuditLogsByAction_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByAction("CREATE")).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByAction("CREATE", "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
        assertEquals("CREATE", responses.get(0).action());
    }

    @Test
    void getAuditLogsByDateRange_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByDateRange(start, end)).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByDateRange(start, end, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
    }

    @Test
    void getAuditLogsByDateRange_ValidationError() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        when(auditService.getAuditLogsByDateRange(start, end)).thenThrow(new IllegalArgumentException("audit.dateRange.required"));
        when(messageSource.getMessage("audit.dateRange.required", null, "Unknown error", Locale.ENGLISH))
            .thenReturn("Date range is required");

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByDateRange(start, end, "en");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody() instanceof ErrorResponse);
        ErrorResponse response = (ErrorResponse) result.getBody();
        assertEquals("Date range is required", response.error());
    }

    @Test
    void getAuditLogsByUserAndDateRange_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditService.getAuditLogsByUserAndDateRange("user123", start, end)).thenReturn(List.of(auditLog));

        // When
        ResponseEntity<Object> result = controller.getAuditLogsByUserAndDateRange("user123", start, end, "en");

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<AuditLogResponse> responses = (List<AuditLogResponse>) result.getBody();
        assertEquals(1, responses.size());
    }
}
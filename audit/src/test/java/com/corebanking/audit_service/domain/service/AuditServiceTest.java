package com.corebanking.audit_service.domain.service;

import com.corebanking.audit_service.domain.model.AuditLog;
import com.corebanking.audit_service.domain.port.AuditRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepositoryPort auditRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void createAuditLog_Success() {
        // Given
        AuditLog savedAuditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.save(any(AuditLog.class))).thenReturn(savedAuditLog);

        // When
        AuditLog result = auditService.createAuditLog(
            "user123", "CREATE", "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0"
        );

        // Then
        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals("CREATE", result.getAction());
        assertEquals("USER", result.getEntityType());
        assertEquals("user456", result.getEntityId());
        assertEquals("Created user", result.getDetails());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        verify(auditRepository).save(any(AuditLog.class));
    }

    @Test
    void createAuditLog_UserIdNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog(null, "CREATE", "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void createAuditLog_UserIdBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("", "CREATE", "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void createAuditLog_UserIdWhitespace() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("   ", "CREATE", "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void createAuditLog_ActionNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("user123", null, "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.action.required", exception.getMessage());
    }

    @Test
    void createAuditLog_ActionBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("user123", "", "USER", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.action.required", exception.getMessage());
    }

    @Test
    void createAuditLog_EntityTypeNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("user123", "CREATE", null, "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void createAuditLog_EntityTypeBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.createAuditLog("user123", "CREATE", "", "user456", "Created user", "192.168.1.1", "Mozilla/5.0")
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void getAuditLogById_Found() {
        // Given
        UUID id = UUID.randomUUID();
        AuditLog auditLog = new AuditLog(
            id, "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.findById(id)).thenReturn(Optional.of(auditLog));

        // When
        Optional<AuditLog> result = auditService.getAuditLogById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(auditRepository).findById(id);
    }

    @Test
    void getAuditLogById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(auditRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<AuditLog> result = auditService.getAuditLogById(id);

        // Then
        assertFalse(result.isPresent());
        verify(auditRepository).findById(id);
    }

    @Test
    void getAuditLogsByUserId_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.findByUserId("user123")).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByUserId("user123");

        // Then
        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getUserId());
        verify(auditRepository).findByUserId("user123");
    }

    @Test
    void getAuditLogsByUserId_UserIdNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserId(null)
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByUserId_UserIdBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserId("")
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntityType_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.findByEntityType("USER")).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByEntityType("USER");

        // Then
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getEntityType());
        verify(auditRepository).findByEntityType("USER");
    }

    @Test
    void getAuditLogsByEntityType_EntityTypeNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntityType(null)
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntityType_EntityTypeBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntityType("")
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntity_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.findByEntityTypeAndEntityId("USER", "user456")).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByEntity("USER", "user456");

        // Then
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getEntityType());
        assertEquals("user456", result.get(0).getEntityId());
        verify(auditRepository).findByEntityTypeAndEntityId("USER", "user456");
    }

    @Test
    void getAuditLogsByEntity_EntityTypeNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntity(null, "user456")
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntity_EntityTypeBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntity("", "user456")
        );
        assertEquals("audit.entityType.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntity_EntityIdNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntity("USER", null)
        );
        assertEquals("audit.entityId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByEntity_EntityIdBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByEntity("USER", "")
        );
        assertEquals("audit.entityId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByAction_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456",
            "Created user", LocalDateTime.now(), "192.168.1.1", "Mozilla/5.0"
        );
        when(auditRepository.findByAction("CREATE")).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByAction("CREATE");

        // Then
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
        verify(auditRepository).findByAction("CREATE");
    }

    @Test
    void getAuditLogsByAction_ActionNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByAction(null)
        );
        assertEquals("audit.action.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByAction_ActionBlank() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByAction("")
        );
        assertEquals("audit.action.required", exception.getMessage());
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
        when(auditRepository.findByTimestampBetween(start, end)).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByDateRange(start, end);

        // Then
        assertEquals(1, result.size());
        verify(auditRepository).findByTimestampBetween(start, end);
    }

    @Test
    void getAuditLogsByDateRange_StartNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByDateRange(null, LocalDateTime.now())
        );
        assertEquals("audit.dateRange.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByDateRange_EndNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByDateRange(LocalDateTime.now(), null)
        );
        assertEquals("audit.dateRange.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByDateRange_StartAfterEnd() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 23, 59);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByDateRange(start, end)
        );
        assertEquals("audit.dateRange.invalid", exception.getMessage());
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
        when(auditRepository.findByUserIdAndTimestampBetween("user123", start, end)).thenReturn(List.of(auditLog));

        // When
        List<AuditLog> result = auditService.getAuditLogsByUserAndDateRange("user123", start, end);

        // Then
        assertEquals(1, result.size());
        verify(auditRepository).findByUserIdAndTimestampBetween("user123", start, end);
    }

    @Test
    void getAuditLogsByUserAndDateRange_UserIdNull() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserAndDateRange(null, start, end)
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByUserAndDateRange_UserIdBlank() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserAndDateRange("", start, end)
        );
        assertEquals("audit.userId.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByUserAndDateRange_StartNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserAndDateRange("user123", null, LocalDateTime.now())
        );
        assertEquals("audit.dateRange.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByUserAndDateRange_EndNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserAndDateRange("user123", LocalDateTime.now(), null)
        );
        assertEquals("audit.dateRange.required", exception.getMessage());
    }

    @Test
    void getAuditLogsByUserAndDateRange_StartAfterEnd() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 12, 31, 23, 59);
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 0, 0);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            auditService.getAuditLogsByUserAndDateRange("user123", start, end)
        );
        assertEquals("audit.dateRange.invalid", exception.getMessage());
    }
}
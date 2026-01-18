package com.corebanking.audit_service.adapter.persistence;

import com.corebanking.audit_service.domain.model.AuditLog;
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
class JpaAuditRepositoryAdapterTest {

    @Mock
    private AuditLogJpaRepository jpaRepository;

    @InjectMocks
    private JpaAuditRepositoryAdapter adapter;

    @Test
    void save_Success() {
        // Given
        AuditLog auditLog = new AuditLog(
            "user123", "CREATE", "USER", "user456", "Created user",
            LocalDateTime.of(2023, 1, 1, 12, 0), "192.168.1.1", "Mozilla/5.0"
        );
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.save(any(AuditLogEntity.class))).thenReturn(entity);

        // When
        AuditLog result = adapter.save(auditLog);

        // Then
        assertNotNull(result);
        assertEquals("user123", result.getUserId());
        assertEquals("CREATE", result.getAction());
        assertEquals("USER", result.getEntityType());
        assertEquals("user456", result.getEntityId());
        assertEquals("Created user", result.getDetails());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
        verify(jpaRepository).save(any(AuditLogEntity.class));
    }

    @Test
    void save_WithExistingId() {
        // Given
        UUID existingId = UUID.randomUUID();
        AuditLog auditLog = new AuditLog(
            existingId, "user123", "CREATE", "USER", "user456", "Created user",
            LocalDateTime.of(2023, 1, 1, 12, 0), "192.168.1.1", "Mozilla/5.0"
        );
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(existingId);
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.save(any(AuditLogEntity.class))).thenReturn(entity);

        // When
        AuditLog result = adapter.save(auditLog);

        // Then
        assertEquals(existingId, result.getId());
        verify(jpaRepository).save(any(AuditLogEntity.class));
    }

    @Test
    void findById_Found() {
        // Given
        UUID id = UUID.randomUUID();
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(id);
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<AuditLog> result = adapter.findById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        assertEquals("user123", result.get().getUserId());
        assertEquals("CREATE", result.get().getAction());
        assertEquals("USER", result.get().getEntityType());
        assertEquals("user456", result.get().getEntityId());
        assertEquals("Created user", result.get().getDetails());
        assertEquals("192.168.1.1", result.get().getIpAddress());
        assertEquals("Mozilla/5.0", result.get().getUserAgent());
        verify(jpaRepository).findById(id);
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<AuditLog> result = adapter.findById(id);

        // Then
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(id);
    }

    @Test
    void findByUserId_Success() {
        // Given
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByUserId("user123")).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByUserId("user123");

        // Then
        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getUserId());
        verify(jpaRepository).findByUserId("user123");
    }

    @Test
    void findByEntityType_Success() {
        // Given
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByEntityType("USER")).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByEntityType("USER");

        // Then
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getEntityType());
        verify(jpaRepository).findByEntityType("USER");
    }

    @Test
    void findByEntityTypeAndEntityId_Success() {
        // Given
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByEntityTypeAndEntityId("USER", "user456")).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByEntityTypeAndEntityId("USER", "user456");

        // Then
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getEntityType());
        assertEquals("user456", result.get(0).getEntityId());
        verify(jpaRepository).findByEntityTypeAndEntityId("USER", "user456");
    }

    @Test
    void findByAction_Success() {
        // Given
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 1, 1, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByAction("CREATE")).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByAction("CREATE");

        // Then
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
        verify(jpaRepository).findByAction("CREATE");
    }

    @Test
    void findByTimestampBetween_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 6, 15, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByTimestampBetween(start, end)).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByTimestampBetween(start, end);

        // Then
        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getUserId());
        verify(jpaRepository).findByTimestampBetween(start, end);
    }

    @Test
    void findByUserIdAndTimestampBetween_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(UUID.randomUUID());
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        entity.setTimestamp(LocalDateTime.of(2023, 6, 15, 12, 0));
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        when(jpaRepository.findByUserIdAndTimestampBetween("user123", start, end)).thenReturn(List.of(entity));

        // When
        List<AuditLog> result = adapter.findByUserIdAndTimestampBetween("user123", start, end);

        // Then
        assertEquals(1, result.size());
        assertEquals("user123", result.get(0).getUserId());
        verify(jpaRepository).findByUserIdAndTimestampBetween("user123", start, end);
    }

    @Test
    void toDomain_MapsAllFields() {
        // Given
        AuditLogEntity entity = new AuditLogEntity();
        UUID id = UUID.randomUUID();
        entity.setId(id);
        entity.setUserId("user123");
        entity.setAction("CREATE");
        entity.setEntityType("USER");
        entity.setEntityId("user456");
        entity.setDetails("Created user");
        LocalDateTime timestamp = LocalDateTime.of(2023, 1, 1, 12, 0);
        entity.setTimestamp(timestamp);
        entity.setIpAddress("192.168.1.1");
        entity.setUserAgent("Mozilla/5.0");

        // When
        AuditLog result = adapter.toDomain(entity);

        // Then
        assertEquals(id, result.getId());
        assertEquals("user123", result.getUserId());
        assertEquals("CREATE", result.getAction());
        assertEquals("USER", result.getEntityType());
        assertEquals("user456", result.getEntityId());
        assertEquals("Created user", result.getDetails());
        assertEquals(timestamp, result.getTimestamp());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
    }

    @Test
    void toEntity_MapsAllFields() {
        // Given
        AuditLog auditLog = new AuditLog(
            UUID.randomUUID(), "user123", "CREATE", "USER", "user456", "Created user",
            LocalDateTime.of(2023, 1, 1, 12, 0), "192.168.1.1", "Mozilla/5.0"
        );

        // When
        AuditLogEntity result = adapter.toEntity(auditLog);

        // Then
        assertEquals(auditLog.getId(), result.getId());
        assertEquals("user123", result.getUserId());
        assertEquals("CREATE", result.getAction());
        assertEquals("USER", result.getEntityType());
        assertEquals("user456", result.getEntityId());
        assertEquals("Created user", result.getDetails());
        assertEquals(auditLog.getTimestamp(), result.getTimestamp());
        assertEquals("192.168.1.1", result.getIpAddress());
        assertEquals("Mozilla/5.0", result.getUserAgent());
    }

    @Test
    void toEntity_WithNullId() {
        // Given
        AuditLog auditLog = new AuditLog(
            null, "user123", "CREATE", "USER", "user456", "Created user",
            LocalDateTime.of(2023, 1, 1, 12, 0), "192.168.1.1", "Mozilla/5.0"
        );

        // When
        AuditLogEntity result = adapter.toEntity(auditLog);

        // Then
        assertNull(result.getId());
        assertEquals("user123", result.getUserId());
    }
}
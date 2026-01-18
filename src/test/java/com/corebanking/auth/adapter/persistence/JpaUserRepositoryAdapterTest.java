package com.corebanking.auth.adapter.persistence;

import com.corebanking.auth.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @InjectMocks
    private JpaUserRepositoryAdapter adapter;

    @Test
    void findByUsername_Found() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("testuser");
        entity.setPassword("encoded");
        entity.setRole("USER");
        when(jpaRepository.findByUsername("testuser")).thenReturn(Optional.of(entity));

        // When
        Optional<User> result = adapter.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertEquals("encoded", result.get().getPassword());
        assertEquals("USER", result.get().getRole());
    }

    @Test
    void findByUsername_NotFound() {
        // Given
        when(jpaRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = adapter.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void save_Success() {
        // Given
        User user = new User("testuser", "encoded", "USER");
        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setUsername("testuser");
        savedEntity.setPassword("encoded");
        savedEntity.setRole("USER");
        when(jpaRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // When
        User result = adapter.save(user);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals("USER", result.getRole());
        verify(jpaRepository).save(any(UserEntity.class));
    }

    @Test
    void existsByUsername_True() {
        // Given
        when(jpaRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = adapter.existsByUsername("testuser");

        // Then
        assertTrue(result);
    }

    @Test
    void existsByUsername_False() {
        // Given
        when(jpaRepository.existsByUsername("nonexistent")).thenReturn(false);

        // When
        boolean result = adapter.existsByUsername("nonexistent");

        // Then
        assertFalse(result);
    }
}
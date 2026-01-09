package com.corebanking.auth_service.adapter.persistence;

import com.corebanking.auth_service.domain.model.User;
import com.corebanking.auth_service.domain.port.UserRepositoryPort;

import java.util.Optional;

public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public JpaUserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(e -> new User(e.getUsername(), e.getPassword(), e.getRole()));
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity();
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole());
        UserEntity saved = jpaRepository.save(entity);
        return new User(saved.getUsername(), saved.getPassword(), saved.getRole());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
}

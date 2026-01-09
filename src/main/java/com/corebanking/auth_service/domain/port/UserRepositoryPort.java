package com.corebanking.auth_service.domain.port;

import com.corebanking.auth_service.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
}

package com.corebanking.auth.domain.port;

import com.corebanking.auth.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
}

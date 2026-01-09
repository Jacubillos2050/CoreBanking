package com.corebanking.auth_service.domain.service;

import com.corebanking.auth_service.domain.model.User;
import com.corebanking.auth_service.domain.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthService {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword, role);
        return userRepository.save(user);
    }

    public boolean validateCredentials(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }
}

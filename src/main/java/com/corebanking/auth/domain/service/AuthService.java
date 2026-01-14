package com.corebanking.auth.domain.service;

import com.corebanking.auth.domain.model.User;
import com.corebanking.auth.domain.port.JwtTokenProviderPort;
import com.corebanking.auth.domain.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderPort jwtTokenProvider;

    public AuthService(UserRepositoryPort userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProviderPort jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User register(String username, String rawPassword, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("user.exists");
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
    public String authenticate(String username, String rawPassword) {
        if (!validateCredentials(username, rawPassword)) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        // Obtener rol del usuario (necesitas cargarlo)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return jwtTokenProvider.generateToken(user.getUsername(), user.getRole());
    }
}

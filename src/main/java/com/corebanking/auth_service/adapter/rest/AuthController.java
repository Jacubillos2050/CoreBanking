package com.corebanking.auth_service.adapter.rest;

import com.corebanking.auth_service.domain.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String healthCheck() {
        return "Auth Service is running! Use POST /register to create users.";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.username(), request.password(), request.role());
        return ResponseEntity.ok("User registered successfully");
    }

    // login con JWT
}

record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role
) {}

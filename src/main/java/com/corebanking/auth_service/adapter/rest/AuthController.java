package com.corebanking.auth_service.adapter.rest;

import com.corebanking.auth_service.domain.model.User;
import com.corebanking.auth_service.domain.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    public AuthController(AuthService authService, MessageSource messageSource) {
        this.authService = authService;
        this.messageSource = messageSource;
    }
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage) {

        Locale locale = acceptLanguage != null ? Locale.forLanguageTag(acceptLanguage) : Locale.ENGLISH;

        try {
            User user = authService.register(request.username(), request.password(), request.role());

            UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getRole());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            String key = e.getMessage();
            String message = messageSource.getMessage(key, null, "Unknown error", locale);
            return ResponseEntity.badRequest().body(new ErrorResponse(message));
        }
    }
}

// DTOs
record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role
) {}

record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
package com.corebanking.auth.domain.port;

public interface JwtTokenProviderPort {

    String generateToken(String username, String role);
    String getUsernameFromToken(String token);
    String getRoleFromToken(String token);
    boolean validateToken(String token);
}

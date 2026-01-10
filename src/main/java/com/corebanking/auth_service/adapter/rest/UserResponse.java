package com.corebanking.auth_service.adapter.rest;

public record UserResponse(

    Long id,
    String username,
    String role
) {}

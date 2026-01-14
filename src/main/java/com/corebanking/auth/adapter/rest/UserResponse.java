package com.corebanking.auth.adapter.rest;

public record UserResponse(

    Long id,
    String username,
    String role
) {}

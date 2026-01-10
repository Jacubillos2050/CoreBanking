package com.corebanking.auth_service.domain.model;

public class User {
    private Long id;
    private String username;
    private String password;
    private String role;

    public User(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this(null, username, password, role);
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
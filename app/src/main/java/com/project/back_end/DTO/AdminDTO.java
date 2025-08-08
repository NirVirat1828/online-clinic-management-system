package com.project.back_end.DTO;

import java.time.LocalDateTime;

/**
 * Admin API representation (never exposes password hash).
 */
public class AdminDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private boolean admin;

    public Long getId() {
        return id;
    }

    public AdminDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AdminDTO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AdminDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRole() {
        return role;
    }

    public AdminDTO setRole(String role) {
        this.role = role;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AdminDTO setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public boolean isAdmin() {
        return admin;
    }

    public AdminDTO setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }
}
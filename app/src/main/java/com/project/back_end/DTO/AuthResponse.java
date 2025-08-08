package com.project.back_end.DTO;

import java.time.LocalDateTime;

/**
 * Standard authentication response.
 */
public class AuthResponse {

    private String token;
    private LocalDateTime expiresAt;
    private Long userId;
    private String role;
    private String displayName;

    public String getToken() {
        return token;
    }

    public AuthResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public AuthResponse setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AuthResponse setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getRole() {
        return role;
    }

    public AuthResponse setRole(String role) {
        this.role = role;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AuthResponse setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
}
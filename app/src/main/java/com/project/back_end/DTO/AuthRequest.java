package com.project.back_end.DTO;

import jakarta.validation.constraints.NotBlank;

/**
 * Generic authentication request (can be username or email depending on user type).
 */
public class AuthRequest {

    @NotBlank
    private String principal; // username or email

    @NotBlank
    private String password;

    // Optional hint for role (e.g., "ADMIN", "PATIENT", "DOCTOR")
    private String role;

    public String getPrincipal() {
        return principal;
    }

    public AuthRequest setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public AuthRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() {
        return role;
    }

    public AuthRequest setRole(String role) {
        this.role = role;
        return this;
    }
}


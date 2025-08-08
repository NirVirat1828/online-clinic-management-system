package com.project.back_end.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Admin entity mapped to the `admins` table as defined in schema-design.md.
 *
 * Schema:
 *  - id: INT, PRIMARY KEY, AUTO_INCREMENT
 *  - username: VARCHAR(50), NOT NULL, UNIQUE
 *  - password_hash: VARCHAR(255), NOT NULL
 *  - email: VARCHAR(100), NOT NULL, UNIQUE
 *  - role: ENUM('Admin', 'Staff'), NOT NULL, DEFAULT 'Staff'
 *  - created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
 *
 * Notes:
 *  - Password stored as a hash; never expose in responses (WRITE_ONLY).
 *  - Uses Enum for role with STRING mapping.
 *  - createdAt set automatically on persist if not provided.
 */
@Entity
@Table(
        name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_admin_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_admin_email", columnNames = "email")
        }
)
public class Admin {

    public enum Role {
        Admin, Staff
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    /**
     * Stored hashed password.
     * Field name in Java: passwordHash
     * Column name in DB: password_hash
     * Exposed via JSON only on write (never serialized back in responses).
     */
    @NotBlank
    @Size(max = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.Staff;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Constructors
    public Admin() {
    }

    public Admin(String username, String passwordHash, String email, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role != null ? role : Role.Staff;
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = Role.Staff;
        }
    }

    // Getters & Setters (fluent style optional; here standard)

    public Long getId() {
        return id;
    }

    public Admin setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Admin setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Admin setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Admin setEmail(String email) {
        this.email = email;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public Admin setRole(Role role) {
        this.role = role;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Admin setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    // Convenience methods
    public boolean isAdmin() {
        return role == Role.Admin;
    }

    public boolean isStaff() {
        return role == Role.Staff;
    }

    // equals & hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin admin)) return false;
        return id != null && Objects.equals(id, admin.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // toString (avoid exposing passwordHash)
    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
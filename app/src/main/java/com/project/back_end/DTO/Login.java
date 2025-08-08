package com.project.back_end.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Login DTO used for authentication requests.
 *
 * Fields:
 *  - email: user identifier (validated as an email)
 *  - password: raw password provided in the login attempt
 *
 * Notes:
 *  - Password MUST be hashed & checked in the service layer (never store raw).
 *  - Consider renaming to LoginRequest and moving package to `com.project.back_end.dto`
 *    to follow Java package lowercase convention.
 */
public class Login {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters.")
    private String password;

    public Login() {
    }

    public String getEmail() {
        return email;
    }

    public Login setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Login setPassword(String password) {
        this.password = password;
        return this;
    }
}
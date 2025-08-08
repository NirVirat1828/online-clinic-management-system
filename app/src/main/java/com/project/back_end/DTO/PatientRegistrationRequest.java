package com.project.back_end.DTO;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request to register a new patient.
 * Password included only if you later add a credential column (e.g., password_hash).
 */
public class PatientRegistrationRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    private String gender;

    @NotBlank
    @Pattern(regexp = "^[+0-9()\\-\\s]{7,20}$")
    private String phone;

    @Email
    private String email;

    @Size(max = 255)
    private String address;

    // Optional password (hashing to be done in service)
    @Size(min = 6, max = 100)
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public PatientRegistrationRequest setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PatientRegistrationRequest setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public PatientRegistrationRequest setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public PatientRegistrationRequest setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public PatientRegistrationRequest setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PatientRegistrationRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PatientRegistrationRequest setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public PatientRegistrationRequest setPassword(String password) {
        this.password = password;
        return this;
    }
}
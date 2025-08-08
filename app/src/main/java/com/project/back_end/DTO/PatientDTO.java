package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Patient API representation.
 */
public class PatientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public PatientDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public PatientDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PatientDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public PatientDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public PatientDTO setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public PatientDTO setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public PatientDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public PatientDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public PatientDTO setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public PatientDTO setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
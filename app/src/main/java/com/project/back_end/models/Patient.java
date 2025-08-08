package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Patient entity mapped to `patients` table.
 *
 * Schema (patients):
 *  - id: INT, PK, AUTO_INCREMENT
 *  - first_name: VARCHAR(50), NOT NULL
 *  - last_name: VARCHAR(50), NOT NULL
 *  - date_of_birth: DATE, NOT NULL
 *  - gender: ENUM('Male','Female','Other'), NOT NULL
 *  - phone: VARCHAR(20), NOT NULL, UNIQUE
 *  - email: VARCHAR(100), UNIQUE
 *  - address: VARCHAR(255)
 *  - created_at: DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP
 *
 * Notes:
 *  - Using an enum Gender for type safety.
 *  - Uniqueness on phone (and optionally email).
 *  - Password is NOT in the schema; if authentication is needed directly for patients,
 *    add a hashed password field (e.g., passwordHash) intentionally and update schema.
 */
@Entity
@Table(
        name = "patients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uk_patient_email", columnNames = "email")
        }
)
public class Patient {

    public enum Gender {
        Male, Female, Other
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotNull
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @NotBlank
    @Size(max = 20)
    @Pattern(
            regexp = "^[+0-9()\\-\\s]{7,20}$",
            message = "Phone must contain 7–20 valid characters (+ digits - () space)."
    )
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // Constructors
    public Patient() {}

    public Patient(String firstName,
                   String lastName,
                   LocalDate dateOfBirth,
                   Gender gender,
                   String phone,
                   String email,
                   String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    @PrePersist
    private void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters / Setters (fluent optional)
    public Long getId() {
        return id;
    }

    public Patient setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Patient setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Patient setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Patient setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Patient setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Patient setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Patient setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Patient setAddress(String address) {
        this.address = address;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Patient setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Transient
    public String getFullName() {
        return (firstName == null ? "" : firstName.trim()) +
                " " +
                (lastName == null ? "" : lastName.trim());
    }

    // equals/hashCode by id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return id != null && id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // toString (avoid sensitive/potential large relations)
    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Objects;

/**
 * Doctor entity mapped to the `doctors` table as defined in schema-design.md.
 *
 * Schema Reference (doctors):
 *  - id (PK, AUTO_INCREMENT)
 *  - first_name (VARCHAR(50), NOT NULL)
 *  - last_name (VARCHAR(50), NOT NULL)
 *  - specialty (VARCHAR(100), NOT NULL)
 *  - phone (VARCHAR(20), NOT NULL, UNIQUE)
 *  - email (VARCHAR(100), NOT NULL, UNIQUE)
 *  - clinic_location_id (FK → clinic_locations.id)  (nullable per schema line: not marked NOT NULL)
 *  - active (BOOLEAN, NOT NULL, DEFAULT TRUE)
 */
@Entity
@Table(
        name = "doctors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctor_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uk_doctor_email", columnNames = "email")
        }
)
public class Doctor {

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

    @NotBlank
    @Size(max = 100)
    @Column(name = "specialty", nullable = false, length = 100)
    private String specialty;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[+0-9()\\-\\s]{7,20}$",
            message = "Phone number must contain only digits and common phone symbols, length 7–20.")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    /**
     * Many doctors can belong to one clinic location.
     * The schema does not mark clinic_location_id as NOT NULL, so it is optional here.
     *
     * If you create a ClinicLocation entity, ensure its @Entity name/table matches `clinic_locations`.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_location_id",
            foreignKey = @ForeignKey(name = "fk_doctor_clinic_location"))
    private ClinicLocation clinicLocation;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    // Constructors
    public Doctor() {
    }

    public Doctor(String firstName,
                  String lastName,
                  String specialty,
                  String phone,
                  String email,
                  ClinicLocation clinicLocation,
                  Boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.phone = phone;
        this.email = email;
        this.clinicLocation = clinicLocation;
        this.active = active != null ? active : Boolean.TRUE;
    }

    // Convenience constructor without clinic location
    public Doctor(String firstName,
                  String lastName,
                  String specialty,
                  String phone,
                  String email) {
        this(firstName, lastName, specialty, phone, email, null, Boolean.TRUE);
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public Doctor setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Doctor setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Doctor setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getSpecialty() {
        return specialty;
    }

    public Doctor setSpecialty(String specialty) {
        this.specialty = specialty;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Doctor setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Doctor setEmail(String email) {
        this.email = email;
        return this;
    }

    public ClinicLocation getClinicLocation() {
        return clinicLocation;
    }

    public Doctor setClinicLocation(ClinicLocation clinicLocation) {
        this.clinicLocation = clinicLocation;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public Doctor setActive(Boolean active) {
        this.active = active;
        return this;
    }

    // Derived / convenience methods

    @Transient
    public String getFullName() {
        return (firstName == null ? "" : firstName.trim()) +
                " " +
                (lastName == null ? "" : lastName.trim());
    }

    // equals & hashCode based on id (entity identity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return id != null && Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // toString (avoid loading lazy relations)
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", specialty='" + specialty + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                (clinicLocation != null ? ", clinicLocationId=" + clinicLocation.getId() : "") +
                '}';
    }
}
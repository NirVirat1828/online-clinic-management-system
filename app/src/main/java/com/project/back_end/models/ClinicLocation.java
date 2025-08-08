package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ClinicLocation entity mapped to the `clinic_locations` table.
 */
@Entity
@Table(name = "clinic_locations")
public class ClinicLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Size(max = 20)
    @Pattern(
            regexp = "^$|^[+0-9()\\-\\s]{7,20}$",
            message = "Phone must be empty or contain 7–20 valid phone characters."
    )
    @Column(name = "phone", length = 20)
    private String phone;

    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    // Removed cascade = CascadeType.NONE (invalid). Omitting cascade means no cascading.
    @OneToMany(mappedBy = "clinicLocation")
    private List<Doctor> doctors = new ArrayList<>();

    public ClinicLocation() {
    }

    public ClinicLocation(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public ClinicLocation(String name, String address, String phone, String email) {
        this(name, address);
        this.phone = phone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public ClinicLocation setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClinicLocation setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ClinicLocation setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ClinicLocation setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ClinicLocation setEmail(String email) {
        this.email = email;
        return this;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public ClinicLocation setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
        return this;
    }

    public void addDoctor(Doctor doctor) {
        if (doctor == null) return;
        doctors.add(doctor);
        doctor.setClinicLocation(this);
    }

    public void removeDoctor(Doctor doctor) {
        if (doctor == null) return;
        doctors.remove(doctor);
        if (doctor.getClinicLocation() == this) {
            doctor.setClinicLocation(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClinicLocation that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ClinicLocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                (phone != null ? ", phone='" + phone + '\'' : "") +
                (email != null ? ", email='" + email + '\'' : "") +
                '}';
    }
}
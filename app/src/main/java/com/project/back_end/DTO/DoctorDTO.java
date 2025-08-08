package com.project.back_end.DTO;

/**
 * Doctor API representation.
 */
public class DoctorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String specialty;
    private String phone;
    private String email;
    private Long clinicLocationId;
    private String clinicLocationName;
    private Boolean active;

    public Long getId() {
        return id;
    }

    public DoctorDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public DoctorDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public DoctorDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public DoctorDTO setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getSpecialty() {
        return specialty;
    }

    public DoctorDTO setSpecialty(String specialty) {
        this.specialty = specialty;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public DoctorDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public DoctorDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getClinicLocationId() {
        return clinicLocationId;
    }

    public DoctorDTO setClinicLocationId(Long clinicLocationId) {
        this.clinicLocationId = clinicLocationId;
        return this;
    }

    public String getClinicLocationName() {
        return clinicLocationName;
    }

    public DoctorDTO setClinicLocationName(String clinicLocationName) {
        this.clinicLocationName = clinicLocationName;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public DoctorDTO setActive(Boolean active) {
        this.active = active;
        return this;
    }
}
package com.project.back_end.DTO;

/**
 * Clinic Location representation.
 */
public class ClinicLocationDTO {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private Integer doctorCount; // optional aggregated value

    public Long getId() {
        return id;
    }

    public ClinicLocationDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ClinicLocationDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ClinicLocationDTO setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ClinicLocationDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ClinicLocationDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public Integer getDoctorCount() {
        return doctorCount;
    }

    public ClinicLocationDTO setDoctorCount(Integer doctorCount) {
        this.doctorCount = doctorCount;
        return this;
    }
}
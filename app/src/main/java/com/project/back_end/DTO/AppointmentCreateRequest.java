package com.project.back_end.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request body for booking a new appointment.
 */
public class AppointmentCreateRequest {

    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;

    @NotNull
    private Long clinicLocationId;

    @NotNull
    @Future
    private LocalDateTime appointmentTime;

    public Long getDoctorId() {
        return doctorId;
    }

    public AppointmentCreateRequest setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public Long getPatientId() {
        return patientId;
    }

    public AppointmentCreateRequest setPatientId(Long patientId) {
        this.patientId = patientId;
        return this;
    }

    public Long getClinicLocationId() {
        return clinicLocationId;
    }

    public AppointmentCreateRequest setClinicLocationId(Long clinicLocationId) {
        this.clinicLocationId = clinicLocationId;
        return this;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentCreateRequest setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }
}
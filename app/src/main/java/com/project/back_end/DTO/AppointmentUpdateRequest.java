package com.project.back_end.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request for updating an existing appointment's time (and optionally doctor/location).
 */
public class AppointmentUpdateRequest {

    @NotNull
    private Long appointmentId;

    @Future
    @NotNull
    private LocalDateTime newAppointmentTime;

    private Long newDoctorId;
    private Long newClinicLocationId;

    public Long getAppointmentId() {
        return appointmentId;
    }

    public AppointmentUpdateRequest setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public LocalDateTime getNewAppointmentTime() {
        return newAppointmentTime;
    }

    public AppointmentUpdateRequest setNewAppointmentTime(LocalDateTime newAppointmentTime) {
        this.newAppointmentTime = newAppointmentTime;
        return this;
    }

    public Long getNewDoctorId() {
        return newDoctorId;
    }

    public AppointmentUpdateRequest setNewDoctorId(Long newDoctorId) {
        this.newDoctorId = newDoctorId;
        return this;
    }

    public Long getNewClinicLocationId() {
        return newClinicLocationId;
    }

    public AppointmentUpdateRequest setNewClinicLocationId(Long newClinicLocationId) {
        this.newClinicLocationId = newClinicLocationId;
        return this;
    }
}
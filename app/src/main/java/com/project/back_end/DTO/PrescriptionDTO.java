package com.project.back_end.DTO;

import java.time.LocalDateTime;

/**
 * Prescription representation for API.
 */
public class PrescriptionDTO {

    private String id;
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private LocalDateTime prescribedAt;
    private String notes;

    public String getId() {
        return id;
    }

    public PrescriptionDTO setId(String id) {
        this.id = id;
        return this;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public PrescriptionDTO setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public PrescriptionDTO setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public Long getPatientId() {
        return patientId;
    }

    public PrescriptionDTO setPatientId(Long patientId) {
        this.patientId = patientId;
        return this;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public PrescriptionDTO setPrescribedAt(LocalDateTime prescribedAt) {
        this.prescribedAt = prescribedAt;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public PrescriptionDTO setNotes(String notes) {
        this.notes = notes;
        return this;
    }
}
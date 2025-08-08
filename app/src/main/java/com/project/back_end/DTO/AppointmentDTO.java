package com.project.back_end.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Read-only data representation of an Appointment for API responses.
 */
public class AppointmentDTO {

    private Long id;
    private Long doctorId;
    private String doctorFullName;
    private Long patientId;
    private String patientFullName;
    private Long clinicLocationId;
    private String clinicLocationName;
    private LocalDateTime appointmentTime;
    private LocalDate appointmentDate;
    private LocalTime appointmentTimeOnly;
    private LocalDateTime endTime;
    private Integer status;
    private String statusLabel;
    private LocalDateTime createdAt;

    public AppointmentDTO() {}

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public AppointmentDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public AppointmentDTO setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public AppointmentDTO setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
        return this;
    }

    public Long getPatientId() {
        return patientId;
    }

    public AppointmentDTO setPatientId(Long patientId) {
        this.patientId = patientId;
        return this;
    }

    public String getPatientFullName() {
        return patientFullName;
    }

    public AppointmentDTO setPatientFullName(String patientFullName) {
        this.patientFullName = patientFullName;
        return this;
    }

    public Long getClinicLocationId() {
        return clinicLocationId;
    }

    public AppointmentDTO setClinicLocationId(Long clinicLocationId) {
        this.clinicLocationId = clinicLocationId;
        return this;
    }

    public String getClinicLocationName() {
        return clinicLocationName;
    }

    public AppointmentDTO setClinicLocationName(String clinicLocationName) {
        this.clinicLocationName = clinicLocationName;
        return this;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public AppointmentDTO setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public AppointmentDTO setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
        return this;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public AppointmentDTO setAppointmentTimeOnly(LocalTime appointmentTimeOnly) {
        this.appointmentTimeOnly = appointmentTimeOnly;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public AppointmentDTO setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public AppointmentDTO setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public AppointmentDTO setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AppointmentDTO setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
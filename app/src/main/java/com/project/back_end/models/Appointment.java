package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Appointment entity mapped to the `appointments` table.
 *
 * Schema Reference (appointments):
 *  - id (PK, AUTO_INCREMENT)
 *  - doctor_id (FK → doctors.id)        NOT NULL
 *  - patient_id (FK → patients.id)      NOT NULL
 *  - clinic_location_id (FK → clinic_locations.id) NOT NULL
 *  - appointment_time (DATETIME)        NOT NULL
 *  - status (ENUM / application int)    NOT NULL  (Scheduled, Completed, Cancelled)
 *  - created_at (DATETIME)              NOT NULL, DEFAULT CURRENT_TIMESTAMP
 *
 * NOTE ON STATUS IMPLEMENTATION:
 *  - The original placeholder comments described status as an int (0 = scheduled, 1 = completed).
 *  - The front-end JS (e.g., patientAppointment.js) checks `appointment.status == 0` to allow editing.
 *  - To stay compatible with that code, we keep an int field here and extend it to include:
 *       0 = Scheduled
 *       1 = Completed
 *       2 = Cancelled
 *  - If you later want to align strictly with the schema ENUM, you can:
 *       (a) Change this to an @Enumerated(EnumType.STRING) AppointmentStatus enum
 *       (b) Adjust the JSON serialization or front-end checks.
 */
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_appointment_doctor_time",
                        columnNames = {"doctor_id", "appointment_time"}
                )
        }
)
public class Appointment {

    // ----------------------------------------------------------------
    // Status Constants (to keep numeric usage consistent in the app)
    // ----------------------------------------------------------------
    public static final int STATUS_SCHEDULED = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_CANCELLED = 2;

    // ----------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many appointments can belong to one doctor.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_doctor")
    )
    private Doctor doctor;

    /**
     * Many appointments can belong to one patient.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_patient")
    )
    private Patient patient;

    /**
     * Each appointment occurs at a clinic location.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "clinic_location_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_appointment_clinic_location")
    )
    private ClinicLocation clinicLocation;

    /**
     * The scheduled date & time (must be in the future on creation).
     * Adjust/remove @Future if editing past appointments is required for history.
     */
    @NotNull
    @Future(message = "Appointment time must be in the future.")
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    /**
     * Integer status used by front-end logic (0 editable, etc.).
     * Default: Scheduled (0)
     */
    @NotNull
    @Column(name = "status", nullable = false)
    private Integer status = STATUS_SCHEDULED;

    /**
     * Timestamp when the record was created.
     * Let the database assign CURRENT_TIMESTAMP, but keep it readable in the entity.
     */
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // ----------------------------------------------------------------
    // Lifecycle Callbacks
    // ----------------------------------------------------------------
    @PrePersist
    private void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // ----------------------------------------------------------------
    // Transient Derived Helpers
    // ----------------------------------------------------------------

    /**
     * Calculates an assumed end time (1 hour after start).
     * Not persisted.
     */
    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime != null ? appointmentTime.plusHours(1) : null;
    }

    /**
     * Returns only the date portion of appointmentTime.
     */
    @Transient
    public LocalDate getAppointmentDate() {
        return appointmentTime != null ? appointmentTime.toLocalDate() : null;
    }

    /**
     * Returns only the time portion of appointmentTime.
     */
    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime != null ? appointmentTime.toLocalTime() : null;
    }

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Appointment() {
    }

    public Appointment(Doctor doctor,
                       Patient patient,
                       ClinicLocation clinicLocation,
                       LocalDateTime appointmentTime,
                       Integer status) {
        this.doctor = doctor;
        this.patient = patient;
        this.clinicLocation = clinicLocation;
        this.appointmentTime = appointmentTime;
        this.status = status != null ? status : STATUS_SCHEDULED;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public Appointment setId(Long id) {
        this.id = id;
        return this;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Appointment setDoctor(Doctor doctor) {
        this.doctor = doctor;
        return this;
    }

    public Patient getPatient() {
        return patient;
    }

    public Appointment setPatient(Patient patient) {
        this.patient = patient;
        return this;
    }

    public ClinicLocation getClinicLocation() {
        return clinicLocation;
    }

    public Appointment setClinicLocation(ClinicLocation clinicLocation) {
        this.clinicLocation = clinicLocation;
        return this;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public Appointment setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Appointment setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Appointment setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    // Convenience checks
    @Transient
    public boolean isScheduled() {
        return Integer.valueOf(STATUS_SCHEDULED).equals(status);
    }

    @Transient
    public boolean isCompleted() {
        return Integer.valueOf(STATUS_COMPLETED).equals(status);
    }

    @Transient
    public boolean isCancelled() {
        return Integer.valueOf(STATUS_CANCELLED).equals(status);
    }

    // ----------------------------------------------------------------
    // equals & hashCode (based on id)
    // ----------------------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // ----------------------------------------------------------------
    // toString (avoid triggering lazy loads)
    // ----------------------------------------------------------------
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", doctorId=" + (doctor != null ? doctor.getId() : null) +
                ", patientId=" + (patient != null ? patient.getId() : null) +
                ", clinicLocationId=" + (clinicLocation != null ? clinicLocation.getId() : null) +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Prescription stored in MongoDB (schema section describes a relational style, but
 * your repository comment indicates MongoRepository usage).
 *
 * Fields (aligned with schema intent):
 *  - id: String (Mongo ObjectId as String)
 *  - appointmentId: Long (FK reference to relational appointment)
 *  - doctorId: Long  (FK reference to relational doctor)
 *  - patientId: Long (FK reference to relational patient)
 *  - prescribedAt: LocalDateTime (default now)
 *  - notes: String (free text)
 *
 * Indexes:
 *  - appointmentId indexed for quick lookup
 *  - patientId optionally indexed (add if needed)
 */
@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull
    @Indexed
    private Long appointmentId;

    @NotNull
    private Long doctorId;

    @NotNull
    @Indexed
    private Long patientId;

    @NotNull
    private LocalDateTime prescribedAt = LocalDateTime.now();

    @NotBlank
    private String notes;

    public Prescription() {}

    public Prescription(Long appointmentId,
                        Long doctorId,
                        Long patientId,
                        String notes) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.notes = notes;
        this.prescribedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Prescription setId(String id) {
        this.id = id;
        return this;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public Prescription setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public Prescription setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
        return this;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Prescription setPatientId(Long patientId) {
        this.patientId = patientId;
        return this;
    }

    public LocalDateTime getPrescribedAt() {
        return prescribedAt;
    }

    public Prescription setPrescribedAt(LocalDateTime prescribedAt) {
        this.prescribedAt = prescribedAt;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public Prescription setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", appointmentId=" + appointmentId +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", prescribedAt=" + prescribedAt +
                ", notes='" + (notes != null ? notes : "") + '\'' +
                '}';
    }
}
package com.project.back_end.mappers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

/**
 * Manual mapper for Appointment -> AppointmentDTO.
 */
public final class AppointmentMapper {

    private AppointmentMapper() {}

    public static AppointmentDTO toDTO(Appointment entity) {
        if (entity == null) return null;

        Doctor d = entity.getDoctor();
        Patient p = entity.getPatient();

        return new AppointmentDTO()
                .setId(entity.getId())
                .setDoctorId(d != null ? d.getId() : null)
                .setDoctorFullName(d != null ? d.getFullName() : null)
                .setPatientId(p != null ? p.getId() : null)
                .setPatientFullName(p != null ? p.getFullName() : null)
                .setClinicLocationId(entity.getClinicLocation() != null ? entity.getClinicLocation().getId() : null)
                .setClinicLocationName(entity.getClinicLocation() != null ? entity.getClinicLocation().getName() : null)
                .setAppointmentTime(entity.getAppointmentTime())
                .setAppointmentDate(entity.getAppointmentDate())
                .setAppointmentTimeOnly(entity.getAppointmentTimeOnly())
                .setEndTime(entity.getEndTime())
                .setStatus(entity.getStatus())
                .setStatusLabel(statusLabel(entity.getStatus()))
                .setCreatedAt(entity.getCreatedAt());
    }

    public static String statusLabel(Integer status) {
        if (status == null) return "Unknown";
        return switch (status) {
            case 0 -> "Scheduled";
            case 1 -> "Completed";
            case 2 -> "Cancelled";
            default -> "Unknown";
        };
    }
}
package com.project.back_end.mappers;

import com.project.back_end.DTO.PrescriptionDTO;
import com.project.back_end.models.Prescription;

/**
 * Prescription -> PrescriptionDTO mapper.
 */
public final class PrescriptionMapper {

    private PrescriptionMapper() {}

    public static PrescriptionDTO toDTO(Prescription entity) {
        if (entity == null) return null;
        return new PrescriptionDTO()
                .setId(entity.getId())
                .setAppointmentId(entity.getAppointmentId())
                .setDoctorId(entity.getDoctorId())
                .setPatientId(entity.getPatientId())
                .setPrescribedAt(entity.getPrescribedAt())
                .setNotes(entity.getNotes());
    }
}
package com.project.back_end.mappers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.models.Doctor;

/**
 * Doctor -> DoctorDTO mapper.
 */
public final class DoctorMapper {

    private DoctorMapper() {}

    public static DoctorDTO toDTO(Doctor entity) {
        if (entity == null) return null;
        return new DoctorDTO()
                .setId(entity.getId())
                .setFirstName(entity.getFirstName())
                .setLastName(entity.getLastName())
                .setFullName(entity.getFullName())
                .setSpecialty(entity.getSpecialty())
                .setPhone(entity.getPhone())
                .setEmail(entity.getEmail())
                .setClinicLocationId(entity.getClinicLocation() != null ? entity.getClinicLocation().getId() : null)
                .setClinicLocationName(entity.getClinicLocation() != null ? entity.getClinicLocation().getName() : null)
                .setActive(entity.getActive());
    }
}
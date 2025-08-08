package com.project.back_end.mappers;

import com.project.back_end.DTO.ClinicLocationDTO;
import com.project.back_end.models.ClinicLocation;

/**
 * ClinicLocation -> DTO mapper.
 */
public final class ClinicLocationMapper {

    private ClinicLocationMapper() {}

    public static ClinicLocationDTO toDTO(ClinicLocation entity) {
        if (entity == null) return null;
        return new ClinicLocationDTO()
                .setId(entity.getId())
                .setName(entity.getName())
                .setAddress(entity.getAddress())
                .setPhone(entity.getPhone())
                .setEmail(entity.getEmail())
                .setDoctorCount(entity.getDoctors() != null ? entity.getDoctors().size() : 0);
    }
}
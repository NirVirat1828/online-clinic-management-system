package com.project.back_end.mappers;

import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.models.Patient;

/**
 * Patient -> PatientDTO mapper.
 */
public final class PatientMapper {

    private PatientMapper() {}

    public static PatientDTO toDTO(Patient entity) {
        if (entity == null) return null;
        return new PatientDTO()
                .setId(entity.getId())
                .setFirstName(entity.getFirstName())
                .setLastName(entity.getLastName())
                .setFullName(entity.getFullName())
                .setDateOfBirth(entity.getDateOfBirth())
                .setGender(entity.getGender() != null ? entity.getGender().name() : null)
                .setPhone(entity.getPhone())
                .setEmail(entity.getEmail())
                .setAddress(entity.getAddress())
                .setCreatedAt(entity.getCreatedAt());
    }
}
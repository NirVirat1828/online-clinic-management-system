package com.project.back_end.mappers;

import com.project.back_end.DTO.AdminDTO;
import com.project.back_end.models.Admin;

/**
 * Admin -> AdminDTO mapper.
 */
public final class AdminMapper {

    private AdminMapper() {}

    public static AdminDTO toDTO(Admin entity) {
        if (entity == null) return null;
        return new AdminDTO()
                .setId(entity.getId())
                .setUsername(entity.getUsername())
                .setEmail(entity.getEmail())
                .setRole(entity.getRole() != null ? entity.getRole().name() : null)
                .setCreatedAt(entity.getCreatedAt())
                .setAdmin(entity.getRole() != null && entity.getRole() == Admin.Role.Admin);
    }
}
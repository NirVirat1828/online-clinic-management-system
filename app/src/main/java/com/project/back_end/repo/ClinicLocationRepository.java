package com.project.back_end.repo;

import com.project.back_end.models.ClinicLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ClinicLocation entities.
 */
@Repository
public interface ClinicLocationRepository extends JpaRepository<ClinicLocation, Long> {

    List<ClinicLocation> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);
}
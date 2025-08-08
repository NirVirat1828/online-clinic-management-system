package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Doctor entities.
 *
 * Search utilities included for:
 *  - Email lookup
 *  - Name fragments (first or last)
 *  - Specialty (case-insensitive)
 *  - Combined filtering (name + specialty)
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByPhone(String phone);

    List<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstFragment,
                                                                                   String lastFragment);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    List<Doctor> findByActiveTrue();

    List<Doctor> findByClinicLocation_Id(Long clinicLocationId);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    /**
     * Flexible filter combining partial name and specialty (both optional).
     * Pass null or empty string to skip a criterion.
     */
    @Query("""
           SELECT d
           FROM Doctor d
           WHERE (:name IS NULL OR :name = '' OR 
                 LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR
                 LOWER(d.lastName)  LIKE LOWER(CONCAT('%', :name, '%')))
             AND (:specialty IS NULL OR :specialty = '' OR LOWER(d.specialty) = LOWER(:specialty))
             AND (:activeOnly = false OR d.active = true)
           """)
    List<Doctor> flexibleSearch(String name, String specialty, boolean activeOnly);
}
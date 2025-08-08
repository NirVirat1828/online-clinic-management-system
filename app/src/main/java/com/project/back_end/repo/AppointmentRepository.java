package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Appointment entities.
 *
 * Includes:
 *  - Time window queries
 *  - Patient / Doctor filtered queries
 *  - Status updates
 *  - Conflict detection utilities
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Basic lookups
    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    List<Appointment> findByDoctor_IdAndAppointmentTimeBetween(Long doctorId,
                                                               LocalDateTime startInclusive,
                                                               LocalDateTime endExclusive);

    boolean existsByDoctor_IdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    /**
     * Fetch appointments for a doctor on a specific day (time window).
     */
    @Query("""
           SELECT a
           FROM Appointment a
           WHERE a.doctor.id = :doctorId
             AND a.appointmentTime >= :dayStart
             AND a.appointmentTime < :dayEnd
           ORDER BY a.appointmentTime ASC
           """)
    List<Appointment> findDailyDoctorAppointments(Long doctorId,
                                                  LocalDateTime dayStart,
                                                  LocalDateTime dayEnd);

    /**
     * Same as above but filtered by patient name fragment.
     */
    @Query("""
           SELECT a
           FROM Appointment a
           WHERE a.doctor.id = :doctorId
             AND a.appointmentTime >= :dayStart
             AND a.appointmentTime < :dayEnd
             AND (LOWER(a.patient.firstName) LIKE LOWER(CONCAT('%', :patientName, '%'))
                  OR LOWER(a.patient.lastName) LIKE LOWER(CONCAT('%', :patientName, '%')))
           ORDER BY a.appointmentTime ASC
           """)
    List<Appointment> findDailyDoctorAppointmentsFiltered(Long doctorId,
                                                          LocalDateTime dayStart,
                                                          LocalDateTime dayEnd,
                                                          String patientName);

    /**
     * Filter by doctor name (fragment) & patient id.
     */
    @Query("""
           SELECT a
           FROM Appointment a
           WHERE a.patient.id = :patientId
             AND (LOWER(a.doctor.firstName) LIKE LOWER(CONCAT('%', :doctorName, '%'))
                  OR LOWER(a.doctor.lastName) LIKE LOWER(CONCAT('%', :doctorName, '%')))
           ORDER BY a.appointmentTime DESC
           """)
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    /**
     * Filter by doctor name, patient id, and status.
     */
    @Query("""
           SELECT a
           FROM Appointment a
           WHERE a.patient.id = :patientId
             AND a.status = :status
             AND (LOWER(a.doctor.firstName) LIKE LOWER(CONCAT('%', :doctorName, '%'))
                  OR LOWER(a.doctor.lastName) LIKE LOWER(CONCAT('%', :doctorName, '%')))
           ORDER BY a.appointmentTime DESC
           """)
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName,
                                                              Long patientId,
                                                              int status);

    Optional<Appointment> findByIdAndPatient_Id(Long id, Long patientId);

    // Bulk operations
    @Transactional
    void deleteAllByDoctor_Id(Long doctorId);

    // Status update
    @Transactional
    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, long id);
}
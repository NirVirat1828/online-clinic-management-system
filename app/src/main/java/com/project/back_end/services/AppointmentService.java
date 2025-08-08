package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentCreateRequest;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.AppointmentStatusUpdateRequest;
import com.project.back_end.DTO.AppointmentUpdateRequest;
import com.project.back_end.mappers.AppointmentMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.ClinicLocation;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.ClinicLocationRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Core appointment booking / modification logic.
 */
@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final ClinicLocationRepository clinicLocationRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              ClinicLocationRepository clinicLocationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.clinicLocationRepository = clinicLocationRepository;
    }

    @Transactional
    public AppointmentDTO book(AppointmentCreateRequest req) {
        validateBookingRequest(req);

        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        if (doctor.getActive() != null && !doctor.getActive()) {
            throw new IllegalStateException("Doctor is not active");
        }

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        ClinicLocation location = clinicLocationRepository.findById(req.getClinicLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Clinic location not found"));

        // Optional: enforce doctor belongs to location (uncomment if required)
        // if (doctor.getClinicLocation() == null || !doctor.getClinicLocation().getId().equals(location.getId())) {
        //     throw new IllegalArgumentException("Doctor does not operate at the specified clinic location");
        // }

        if (appointmentRepository.existsByDoctor_IdAndAppointmentTime(doctor.getId(), req.getAppointmentTime())) {
            throw new IllegalStateException("Time slot already booked for doctor");
        }

        Appointment appt = new Appointment()
                .setDoctor(doctor)
                .setPatient(patient)
                .setClinicLocation(location)
                .setAppointmentTime(req.getAppointmentTime())
                .setStatus(Appointment.STATUS_SCHEDULED);

        Appointment saved = appointmentRepository.save(appt);
        return AppointmentMapper.toDTO(saved);
    }

    @Transactional
    public AppointmentDTO update(AppointmentUpdateRequest req, Long requestingPatientId) {
        Appointment appt = appointmentRepository.findById(req.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appt.getPatient().getId().equals(requestingPatientId)) {
            throw new SecurityException("You cannot modify another patient's appointment");
        }

        if (appt.getStatus() != null && appt.getStatus() != Appointment.STATUS_SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be updated");
        }

        if (req.getNewAppointmentTime() != null) {
            if (req.getNewAppointmentTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("New appointment time must be in the future");
            }
            Long doctorIdToCheck = req.getNewDoctorId() != null ? req.getNewDoctorId() : appt.getDoctor().getId();
            if (appointmentRepository.existsByDoctor_IdAndAppointmentTime(doctorIdToCheck, req.getNewAppointmentTime())) {
                throw new IllegalStateException("Requested new time conflicts with existing appointment");
            }
            appt.setAppointmentTime(req.getNewAppointmentTime());
        }

        if (req.getNewDoctorId() != null && !req.getNewDoctorId().equals(appt.getDoctor().getId())) {
            Doctor newDoctor = doctorRepository.findById(req.getNewDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("New doctor not found"));
            if (newDoctor.getActive() != null && !newDoctor.getActive()) {
                throw new IllegalStateException("New doctor is not active");
            }
            appt.setDoctor(newDoctor);
        }

        if (req.getNewClinicLocationId() != null &&
                (appt.getClinicLocation() == null ||
                        !req.getNewClinicLocationId().equals(appt.getClinicLocation().getId()))) {
            ClinicLocation newLocation = clinicLocationRepository.findById(req.getNewClinicLocationId())
                    .orElseThrow(() -> new IllegalArgumentException("New clinic location not found"));
            appt.setClinicLocation(newLocation);
        }

        Appointment saved = appointmentRepository.save(appt);
        return AppointmentMapper.toDTO(saved);
    }

    @Transactional
    public void cancel(Long appointmentId, Long requestingPatientId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appt.getPatient().getId().equals(requestingPatientId)) {
            throw new SecurityException("You cannot cancel another patient's appointment");
        }
        if (appt.getStatus() != Appointment.STATUS_SCHEDULED) {
            throw new IllegalStateException("Only scheduled appointments can be cancelled");
        }
        // Option 1: Soft cancel by status
        appt.setStatus(Appointment.STATUS_CANCELLED);
        appointmentRepository.save(appt);
        // Option 2: Hard delete:
        // appointmentRepository.delete(appt);
    }

    public List<AppointmentDTO> doctorDay(Long doctorId, LocalDate date, String patientNameFilter) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Appointment> list;
        if (patientNameFilter != null && !patientNameFilter.isBlank()) {
            list = appointmentRepository.findDailyDoctorAppointmentsFiltered(
                    doctorId, start, end, patientNameFilter.trim());
        } else {
            list = appointmentRepository.findDailyDoctorAppointments(doctorId, start, end);
        }
        return list.stream().map(AppointmentMapper::toDTO).collect(toList());
    }

    @Transactional
    public void changeStatus(AppointmentStatusUpdateRequest req) {
        Appointment appt = appointmentRepository.findById(req.getAppointmentId())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        appt.setStatus(req.getStatus());
        appointmentRepository.save(appt);
    }

    public List<AppointmentDTO> patientAppointments(Long patientId,
                                                    String doctorName,
                                                    String condition,
                                                    Integer status) {
        List<Appointment> base;

        if (doctorName != null && !doctorName.isBlank() && status != null) {
            base = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(
                    doctorName.trim(), patientId, status);
        } else if (doctorName != null && !doctorName.isBlank()) {
            base = appointmentRepository.filterByDoctorNameAndPatientId(
                    doctorName.trim(), patientId);
        } else if (status != null) {
            base = appointmentRepository.findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                    patientId, status);
        } else {
            base = appointmentRepository.findByPatient_Id(patientId);
        }

        LocalDateTime now = LocalDateTime.now();
        return base.stream()
                .filter(a -> {
                    if (condition == null || condition.isBlank() || "all".equalsIgnoreCase(condition)) return true;
                    boolean future = a.getAppointmentTime().isAfter(now);
                    return switch (condition.toLowerCase()) {
                        case "future", "upcoming" -> future;
                        case "past" -> !future;
                        default -> true;
                    };
                })
                .map(AppointmentMapper::toDTO)
                .collect(toList());
    }

    private void validateBookingRequest(AppointmentCreateRequest req) {
        if (req.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment time required");
        }
        if (req.getAppointmentTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment time must be in the future");
        }
        if (req.getDoctorId() == null || req.getPatientId() == null || req.getClinicLocationId() == null) {
            throw new IllegalArgumentException("Doctor, patient, and clinic location are required");
        }

        // Optionally enforce 15/30 min increments:
        // LocalTime t = req.getAppointmentTime().toLocalTime();
        // if (t.getMinute() % 15 != 0) throw new IllegalArgumentException("Time must align to 15-minute increments");
    }
}
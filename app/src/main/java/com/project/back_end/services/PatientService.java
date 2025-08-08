package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.DTO.PatientRegistrationRequest;
import com.project.back_end.mappers.AppointmentMapper;
import com.project.back_end.mappers.PatientMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Business logic for patient registration, profile, and appointments.
 */
@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public PatientDTO register(PatientRegistrationRequest req) {
        if (patientRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("Phone already registered");
        }
        if (req.getEmail() != null && !req.getEmail().isBlank() && patientRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Patient patient = new Patient()
                .setFirstName(req.getFirstName())
                .setLastName(req.getLastName())
                .setDateOfBirth(req.getDateOfBirth())
                .setGender(Patient.Gender.valueOf(req.getGender()))
                .setPhone(req.getPhone())
                .setEmail(req.getEmail())
                .setAddress(req.getAddress());

        Patient saved = patientRepository.save(patient);
        return PatientMapper.toDTO(saved);
    }

    public PatientDTO getProfileByEmail(String email) {
        return patientRepository.findByEmail(email)
                .map(PatientMapper::toDTO)
                .orElse(null);
    }

    public PatientDTO getProfileFromToken(String token) {
        String email = tokenService.extractSubject(token);
        if (email == null) return null;
        return getProfileByEmail(email);
    }

    public PatientDTO getById(Long id) {
        return patientRepository.findById(id)
                .map(PatientMapper::toDTO)
                .orElse(null);
    }

    public List<AppointmentDTO> getAppointments(Long patientId) {
        return appointmentRepository.findByPatient_Id(patientId)
                .stream()
                .map(AppointmentMapper::toDTO)
                .collect(toList());
    }

    /**
     * Filter appointments by condition (past|future|all) and doctor name fragment (optional).
     */
    public List<AppointmentDTO> filterAppointments(Long patientId, String doctorName, String condition) {
        List<Appointment> base;

        if (doctorName != null && !doctorName.isBlank()) {
            base = appointmentRepository.filterByDoctorNameAndPatientId(doctorName.trim(), patientId);
        } else {
            base = appointmentRepository.findByPatient_Id(patientId);
        }

        LocalDateTime now = LocalDateTime.now();
        return base.stream()
                .filter(a -> {
                    if (condition == null || condition.isBlank() || "all".equalsIgnoreCase(condition)) return true;
                    boolean isFuture = a.getAppointmentTime().isAfter(now);
                    return switch (condition.toLowerCase()) {
                        case "future", "upcoming" -> isFuture;
                        case "past" -> !isFuture;
                        default -> true;
                    };
                })
                .map(AppointmentMapper::toDTO)
                .collect(toList());
    }

    public boolean emailOrPhoneExists(String email, String phone) {
        if (phone != null && patientRepository.existsByPhone(phone)) return true;
        if (email != null && patientRepository.existsByEmail(email)) return true;
        return false;
    }
}
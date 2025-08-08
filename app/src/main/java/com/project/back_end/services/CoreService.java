package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.AuthResponse;
import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.mappers.AppointmentMapper;
import com.project.back_end.mappers.DoctorMapper;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregating / orchestration service providing:
 *  - Token validation
 *  - Admin authentication
 *  - Doctor filtering
 *  - Appointment slot validation
 *  - Patient uniqueness validation
 *  - Patient login
 *  - Patient appointment filtering
 *
 * NOTE:
 *  1. Method return types use ResponseEntity<Map<String,Object>> (or Object) to align with the
 *     explanatory requirements in your instructions. For a cleaner architecture, consider returning
 *     domain DTOs and moving HTTP wrapping to controller layer.
 *  2. Password handling is currently plain-text comparison because no hashing / encoder is configured.
 *     You should introduce a password hashing mechanism (e.g., BCrypt) before production use.
 *  3. Patient entity (as previously defined) has NO password field. If you need patient login,
 *     add a passwordHash field to Patient and adapt validatePatientLogin accordingly.
 */
@Service
@Transactional(readOnly = true)
public class CoreService {

    private static final Logger log = LoggerFactory.getLogger(CoreService.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    // Reuse existing specialized services where helpful
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public CoreService(TokenService tokenService,
                       AdminRepository adminRepository,
                       DoctorRepository doctorRepository,
                       PatientRepository patientRepository,
                       AppointmentRepository appointmentRepository,
                       DoctorService doctorService,
                       PatientService patientService,
                       AppointmentService appointmentService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    /* -------------------------------------------------------------
     * 3. validateToken
     * ------------------------------------------------------------- */
    public ResponseEntity<?> validateToken(String rawToken, String expectedRole) {
        Map<String, Object> body = new HashMap<>();
        if (rawToken == null || rawToken.isBlank()) {
            body.put("valid", false);
            body.put("message", "Token missing");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        boolean valid = tokenService.validateToken(rawToken, expectedRole);
        if (!valid) {
            body.put("valid", false);
            body.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        Claims claims = tokenService.parseClaims(rawToken.substring(rawToken.startsWith("Bearer ") ? 7 : 0));
        body.put("valid", true);
        body.put("role", claims.get("role", String.class));
        body.put("userId", claims.get("uid", Long.class));
        body.put("subject", claims.getSubject());
        return ResponseEntity.ok(body);
    }

    /* -------------------------------------------------------------
     * 4. validateAdmin
     * ------------------------------------------------------------- */
    @Transactional
    public ResponseEntity<?> validateAdmin(String usernameOrEmail, String password) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (usernameOrEmail == null || password == null) {
                body.put("message", "Username/email and password required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }

            Optional<Admin> adminOpt = adminRepository.findByUsername(usernameOrEmail);
            if (adminOpt.isEmpty()) {
                adminOpt = adminRepository.findByEmail(usernameOrEmail);
            }
            if (adminOpt.isEmpty()) {
                body.put("message", "Admin not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            Admin admin = adminOpt.get();

            // Plain equality (replace with password encoder compare)
            // Example: passwordEncoder.matches(password, admin.getPasswordHash())
            if (admin.getPasswordHash() == null || !admin.getPasswordHash().equals(password)) {
                body.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }

            String subject = admin.getEmail() != null ? admin.getEmail() : admin.getUsername();
            String token = tokenService.generateToken(subject, "ADMIN", admin.getId());
            AuthResponse auth = new AuthResponse()
                    .setToken(token)
                    .setUserId(admin.getId())
                    .setRole("ADMIN")
                    .setDisplayName(admin.getUsername());

            body.put("message", "Login successful");
            body.put("auth", auth);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Admin validation error", e);
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    /* -------------------------------------------------------------
     * 5. filterDoctor
     *    Parameters are flexible; any can be null.
     *    If you later include time-slot filtering, you can enhance here.
     * ------------------------------------------------------------- */
    public ResponseEntity<?> filterDoctor(String name,
                                          String specialty,
                                          Boolean activeOnly) {
        Map<String, Object> body = new HashMap<>();
        try {
            List<DoctorDTO> results = doctorService.search(name, specialty, activeOnly);
            body.put("count", results.size());
            body.put("doctors", results);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error filtering doctors", e);
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    /* -------------------------------------------------------------
     * 6. validateAppointment
     *    Returns:
     *      1  -> valid / available
     *      0  -> slot invalid or already booked
     *     -1  -> doctor not found
     *
     *    NOTE: Actual availability (doctor working hours) not implemented because
     *    no time-slot entity exists. If you add one (e.g., DoctorAvailableSlot),
     *    integrate that lookup here.
     * ------------------------------------------------------------- */
    public int validateAppointment(Long doctorId, LocalDateTime requestedTime) {
        if (doctorId == null || requestedTime == null) return 0;
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return -1;

        // Reject past
        if (requestedTime.isBefore(LocalDateTime.now())) {
            return 0;
        }

        boolean exists = appointmentRepository.existsByDoctor_IdAndAppointmentTime(doctorId, requestedTime);
        if (exists) return 0;

        // If availability logic is added, check requestedTime against allowed slots here.

        return 1;
    }

    /* -------------------------------------------------------------
     * 7. validatePatient
     * ------------------------------------------------------------- */
    public boolean validatePatient(String email, String phone) {
        if (phone != null && patientRepository.existsByPhone(phone)) return false;
        if (email != null && !email.isBlank() && patientRepository.existsByEmail(email)) return false;
        return true;
    }

    /* -------------------------------------------------------------
     * 8. validatePatientLogin
     *    NOTE: Patient entity currently has NO password field in provided model.
     *          To enable login, add a passwordHash column/field to Patient.
     *          This implementation assumes a getPasswordHash() method exists.
     * ------------------------------------------------------------- */
    @Transactional
    public ResponseEntity<?> validatePatientLogin(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (email == null || password == null) {
                body.put("message", "Email and password required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }

            Optional<Patient> patientOpt = patientRepository.findByEmail(email);
            if (patientOpt.isEmpty()) {
                body.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            Patient patient = patientOpt.get();

            // Placeholder: patient.getPasswordHash() assumed. Adjust to your implementation.
            try {
                var method = patient.getClass().getMethod("getPasswordHash");
                Object stored = method.invoke(patient);
                if (stored == null || !stored.equals(password)) { // Replace with encoder
                    body.put("message", "Invalid credentials");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
                }
            } catch (NoSuchMethodException ns) {
                body.put("message", "Password authentication not configured for Patient");
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(body);
            }

            String token = tokenService.generateToken(patient.getEmail(), "PATIENT", patient.getId());
            AuthResponse auth = new AuthResponse()
                    .setToken(token)
                    .setRole("PATIENT")
                    .setUserId(patient.getId())
                    .setDisplayName(patient.getFullName());

            body.put("message", "Login successful");
            body.put("auth", auth);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Patient login error", e);
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    /* -------------------------------------------------------------
     * 9. filterPatient
     *    condition: "past" | "future" | "all"
     *    doctorName: partial match
     * ------------------------------------------------------------- */
    public ResponseEntity<?> filterPatient(String token,
                                           String doctorName,
                                           String condition) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (token == null) {
                body.put("message", "Token required");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            String email = tokenService.extractSubject(token);
            if (email == null) {
                body.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            }
            PatientDTO patient = patientService.getProfileByEmail(email);
            if (patient == null) {
                body.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }

            List<AppointmentDTO> filtered = patientService.filterAppointments(
                    patient.getId(),
                    doctorName,
                    condition
            );
            body.put("patientId", patient.getId());
            body.put("count", filtered.size());
            body.put("appointments", filtered);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error filtering patient appointments", e);
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    /* -------------------------------------------------------------
     * Additional helper (not part of original spec):
     * ------------------------------------------------------------- */
    public ResponseEntity<?> doctorDailyAppointments(Long doctorId, LocalDate day, String patientName) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (doctorId == null || day == null) {
                body.put("message", "doctorId and day required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
            List<AppointmentDTO> list = appointmentService.doctorDay(doctorId, day, patientName);
            body.put("count", list.size());
            body.put("appointments", list);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error listing doctor daily appointments", e);
            body.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    /* -------------------------------------------------------------
     * Utility: convert appointments for direct use if needed
     * ------------------------------------------------------------- */
    public List<AppointmentDTO> mapAppointments(List<Appointment> appointments) {
        if (appointments == null) return List.of();
        return appointments.stream().map(AppointmentMapper::toDTO).toList();
    }

    public List<DoctorDTO> mapDoctors(List<Doctor> doctors) {
        if (doctors == null) return List.of();
        return doctors.stream().map(DoctorMapper::toDTO).toList();
    }
}
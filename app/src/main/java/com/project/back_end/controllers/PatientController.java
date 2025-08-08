package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.PatientDTO;
import com.project.back_end.DTO.PatientRegistrationRequest;
import com.project.back_end.services.CoreService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Patient registration, profile & appointment retrieval.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final CoreService coreService;
    private final TokenService tokenService;

    public PatientController(PatientService patientService,
                             CoreService coreService,
                             TokenService tokenService) {
        this.patientService = patientService;
        this.coreService = coreService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<PatientDTO> register(@RequestBody @Valid PatientRegistrationRequest request) {
        return ResponseEntity.ok(patientService.register(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> get(@PathVariable Long id) {
        PatientDTO dto = patientService.getById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientDTO> me(@RequestHeader("Authorization") String token) {
        PatientDTO dto = patientService.getProfileFromToken(token);
        return dto == null ? ResponseEntity.status(401).build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/me/appointments")
    public ResponseEntity<List<AppointmentDTO>> myAppointments(@RequestHeader("Authorization") String token,
                                                               @RequestParam(required = false) String doctorName,
                                                               @RequestParam(required = false, defaultValue = "all") String condition) {
        String email = tokenService.extractSubject(token);
        if (email == null) return ResponseEntity.status(401).build();
        PatientDTO patient = patientService.getProfileByEmail(email);
        if (patient == null) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(patientService.filterAppointments(patient.getId(), doctorName, condition));
    }
}
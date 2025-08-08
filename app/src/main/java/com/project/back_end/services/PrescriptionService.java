package com.project.back_end.services;

import com.project.back_end.DTO.PrescriptionDTO;
import com.project.back_end.mappers.PrescriptionMapper;
import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing prescriptions.
 *
 * Responsibilities:
 *  - Enforce one-prescription-per-appointment.
 *  - Persist new prescription documents.
 *  - Retrieve prescription(s) by appointment ID.
 *
 * NOTE:
 *  - Returns ResponseEntity for now (could be refactored to pure DTO/service layer later).
 */
@Service
public class PrescriptionService {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionService.class);

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public ResponseEntity<?> savePrescription(@Valid Prescription prescription) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (prescription.getAppointmentId() == null) {
                body.put("message", "appointmentId is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }

            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existing.isEmpty()) {
                body.put("message", "A prescription already exists for this appointment.");
                body.put("appointmentId", prescription.getAppointmentId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }

            Prescription saved = prescriptionRepository.save(prescription);
            body.put("message", "Prescription created successfully.");
            body.put("prescription", PrescriptionMapper.toDTO(saved));
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } catch (IllegalArgumentException ex) {
            log.warn("Validation failure creating prescription: {}", ex.getMessage());
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        } catch (Exception e) {
            log.error("Error saving prescription", e);
            body.put("message", "Internal server error while saving prescription.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPrescription(Long appointmentId) {
        Map<String, Object> body = new HashMap<>();
        try {
            if (appointmentId == null) {
                body.put("message", "appointmentId is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }

            List<Prescription> list = prescriptionRepository.findByAppointmentId(appointmentId);

            if (list.isEmpty()) {
                body.put("message", "No prescription found for the given appointment.");
                body.put("appointmentId", appointmentId);
                return ResponseEntity.ok(body);
            }

            List<PrescriptionDTO> dtos = list.stream()
                    .map(PrescriptionMapper::toDTO)
                    .collect(Collectors.toList());

            body.put("appointmentId", appointmentId);
            body.put("count", dtos.size());
            if (dtos.size() == 1) {
                body.put("prescription", dtos.get(0));
            }
            body.put("prescriptions", dtos);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            log.error("Error retrieving prescription for appointmentId={}", appointmentId, e);
            body.put("message", "Internal server error while retrieving prescription.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }
    }

    @Transactional(readOnly = true)
    public PrescriptionDTO findSingleByAppointmentId(Long appointmentId) {
        List<Prescription> list = prescriptionRepository.findByAppointmentId(appointmentId);
        if (list.isEmpty()) return null;
        if (list.size() > 1) {
            log.warn("Data anomaly: more than one prescription for appointmentId={}", appointmentId);
        }
        return PrescriptionMapper.toDTO(list.get(0));
    }
}
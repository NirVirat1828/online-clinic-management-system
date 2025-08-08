package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Prescription create & lookup endpoints.
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Prescription prescription) {
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/by-appointment/{appointmentId}")
    public ResponseEntity<?> byAppointment(@PathVariable Long appointmentId) {
        return prescriptionService.getPrescription(appointmentId);
    }
}
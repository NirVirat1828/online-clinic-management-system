package com.project.back_end.controllers;

import com.project.back_end.services.CoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Validation utility endpoints.
 * (Named 'ValidationFailedController' per request; typically would be 'ValidationController')
 */
@RestController
@RequestMapping("/api/validate")
public class ValidationFailed {

    private final CoreService coreService;

    public ValidationFailed(CoreService coreService) {
        this.coreService = coreService;
    }

    @GetMapping("/appointment-slot")
    public ResponseEntity<Integer> validateSlot(@RequestParam Long doctorId,
                                                @RequestParam String dateTimeIso) {
        LocalDateTime time = LocalDateTime.parse(dateTimeIso);
        return ResponseEntity.ok(coreService.validateAppointment(doctorId, time));
    }

    @GetMapping("/patient-unique")
    public ResponseEntity<Boolean> patientUnique(@RequestParam(required = false) String email,
                                                 @RequestParam(required = false) String phone) {
        return ResponseEntity.ok(coreService.validatePatient(email, phone));
    }
}
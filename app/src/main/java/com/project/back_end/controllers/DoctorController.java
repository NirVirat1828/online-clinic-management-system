package com.project.back_end.controllers;

import com.project.back_end.DTO.DoctorDTO;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.CoreService;
import com.project.back_end.services.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Doctor management & search endpoints.
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final CoreService coreService;

    public DoctorController(DoctorService doctorService,
                            CoreService coreService) {
        this.doctorService = doctorService;
        this.coreService = coreService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> listAll() {
        return ResponseEntity.ok(doctorService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> get(@PathVariable Long id) {
        DoctorDTO dto = doctorService.getDoctor(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> search(@RequestParam(required = false) String name,
                                                  @RequestParam(required = false) String specialty,
                                                  @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(doctorService.search(name, specialty, activeOnly));
    }

    @PostMapping
    public ResponseEntity<DoctorDTO> create(@RequestBody @Valid Doctor doctor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(doctor));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<DoctorDTO> toggleActive(@PathVariable Long id,
                                                  @RequestParam boolean active) {
        return ResponseEntity.ok(doctorService.toggleActive(id, active));
    }

    @PatchMapping("/{id}/assign-clinic/{clinicLocationId}")
    public ResponseEntity<DoctorDTO> assignClinic(@PathVariable Long id,
                                                  @PathVariable Long clinicLocationId) {
        return ResponseEntity.ok(doctorService.assignClinic(id, clinicLocationId));
    }
}
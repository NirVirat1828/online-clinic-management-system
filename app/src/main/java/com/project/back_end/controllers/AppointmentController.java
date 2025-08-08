package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentCreateRequest;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.AppointmentStatusUpdateRequest;
import com.project.back_end.DTO.AppointmentUpdateRequest;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.CoreService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Appointment lifecycle controller.
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CoreService coreService;
    private final TokenService tokenService;

    public AppointmentController(AppointmentService appointmentService,
                                 CoreService coreService,
                                 TokenService tokenService) {
        this.appointmentService = appointmentService;
        this.coreService = coreService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> book(@RequestBody @Valid AppointmentCreateRequest request) {
        return ResponseEntity.ok(appointmentService.book(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id,
                                                 @RequestBody @Valid AppointmentUpdateRequest request,
                                                 @RequestHeader("Authorization") String token) {
        Long patientId = tokenService.extractUserId(token);
        if (patientId == null) return ResponseEntity.status(401).build();
        request.setAppointmentId(id);
        return ResponseEntity.ok(appointmentService.update(request, patientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id,
                                    @RequestHeader("Authorization") String token) {
        Long patientId = tokenService.extractUserId(token);
        if (patientId == null) return ResponseEntity.status(401).build();
        appointmentService.cancel(id, patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> doctorDay(@PathVariable Long doctorId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                          @RequestParam(required = false) String patientName) {
        return ResponseEntity.ok(appointmentService.doctorDay(doctorId, date, patientName));
    }

    @PatchMapping("/status")
    public ResponseEntity<?> changeStatus(@RequestBody @Valid AppointmentStatusUpdateRequest request) {
        appointmentService.changeStatus(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> patientAppointments(@PathVariable Long patientId,
                                                                    @RequestParam(required = false) String doctorName,
                                                                    @RequestParam(required = false) String condition,
                                                                    @RequestParam(required = false) Integer status) {
        return ResponseEntity.ok(appointmentService.patientAppointments(patientId, doctorName, condition, status));
    }
}
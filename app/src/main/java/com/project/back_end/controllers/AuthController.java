package com.project.back_end.controllers;

import com.project.back_end.DTO.AuthRequest;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.CoreService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints for admin & patient plus token validation.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CoreService coreService;
    private final PatientService patientService;
    private final TokenService tokenService;

    public AuthController(CoreService coreService,
                          PatientService patientService,
                          TokenService tokenService) {
        this.coreService = coreService;
        this.patientService = patientService;
        this.tokenService = tokenService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody @Valid AuthRequest request) {
        return coreService.validateAdmin(request.getPrincipal(), request.getPassword());
    }

    @PostMapping("/patient/login")
    public ResponseEntity<?> patientLogin(@RequestBody @Valid Login request) {
        return coreService.validatePatientLogin(request.getEmail(), request.getPassword());
    }

    @GetMapping("/token/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(name = "Authorization", required = false) String authHeader,
                                           @RequestParam(required = false) String expectedRole) {
        return coreService.validateToken(authHeader, expectedRole);
    }
}
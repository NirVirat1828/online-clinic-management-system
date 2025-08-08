package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.CoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin authentication & token validation.
 */
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final CoreService coreService;

    public AdminController(CoreService coreService) {
        this.coreService = coreService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody @Valid Admin admin) {
        String principal = admin.getUsername() != null ? admin.getUsername() : admin.getEmail();
        String password = admin.getPasswordHash(); // Assuming passwordHash temporarily holds raw password
        return coreService.validateAdmin(principal, password);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        return coreService.validateToken(authHeader, "ADMIN");
    }
}
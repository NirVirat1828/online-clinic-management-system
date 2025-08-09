package com.project.back_end.mvc;

import com.project.back_end.services.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * MVC controller for secured dashboard views.
 * Provides simple token (path) based access to Thymeleaf templates.
 * NOTE: For production, prefer header-based token passing instead of embedding in URL.
 */
@Controller
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final CoreService coreService;

    public DashboardController(CoreService coreService) {
        this.coreService = coreService;
    }

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable String token) {
        String normalized = normalize(token);
        ResponseEntity<?> resp = coreService.validateToken(normalized, "ADMIN");
        if (resp.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }
        log.debug("Invalid admin token -> redirect:/");
        return "redirect:/";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        String normalized = normalize(token);
        ResponseEntity<?> resp = coreService.validateToken(normalized, "DOCTOR");
        if (resp.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }
        log.debug("Invalid doctor token -> redirect:/");
        return "redirect:/";
    }

    private String normalize(String raw) {
        if (raw == null || raw.isBlank()) return raw;
        return raw.startsWith("Bearer ") ? raw : "Bearer " + raw;
    }
}
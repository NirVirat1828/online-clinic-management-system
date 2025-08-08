package com.project.back_end.services;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final String secret;
    private final int expirationDays;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository,
                        @Value("${jwt.secret:change-this-default-secret-key-please}") String secret,
                        @Value("${jwt.expiration-days:7}") int expirationDays) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.secret = secret;
        this.expirationDays = expirationDays;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subjectEmailOrUsername, String role, Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(86400L * expirationDays);
        return Jwts.builder()
                .subject(subjectEmailOrUsername)
                .claim("role", role)
                .claim("uid", userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token, String expectedRole) {
        try {
            Claims claims = parseClaims(token);
            String role = claims.get("role", String.class);
            if (expectedRole != null && !expectedRole.equalsIgnoreCase(role)) return false;
            Long uid = claims.get("uid", Long.class);
            if (uid == null) return false;
            return switch (role.toUpperCase()) {
                case "ADMIN" -> adminRepository.findById(uid).isPresent();
                case "DOCTOR" -> doctorRepository.findById(uid).isPresent();
                case "PATIENT" -> patientRepository.findById(uid).isPresent();
                default -> false;
            };
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(clean(token))
                .getPayload();
    }

    public String extractSubject(String token) {
        try { return parseClaims(token).getSubject(); }
        catch (JwtException e) { return null; }
    }

    public String extractRole(String token) {
        try { return parseClaims(token).get("role", String.class); }
        catch (JwtException e) { return null; }
    }

    public Long extractUserId(String token) {
        try { return parseClaims(token).get("uid", Long.class); }
        catch (JwtException e) { return null; }
    }

    public LocalDateTime extractExpiration(String token) {
        try {
            Date exp = parseClaims(token).getExpiration();
            return exp != null ? LocalDateTime.ofInstant(exp.toInstant(), ZoneOffset.UTC) : null;
        } catch (JwtException e) {
            return null;
        }
    }

    private String clean(String token) {
        if (token == null) return null;
        return token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
    }

    public Optional<String> generateTokenForUser(String emailOrUsername, String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> adminRepository.findByUsername(emailOrUsername)
                    .or(() -> adminRepository.findByEmail(emailOrUsername))
                    .map(a -> generateToken(a.getEmail() != null ? a.getEmail() : a.getUsername(), "ADMIN", a.getId()));
            case "DOCTOR" -> doctorRepository.findByEmail(emailOrUsername)
                    .map(d -> generateToken(d.getEmail(), "DOCTOR", d.getId()));
            case "PATIENT" -> patientRepository.findByEmail(emailOrUsername)
                    .map(p -> generateToken(p.getEmail(), "PATIENT", p.getId()));
            default -> Optional.empty();
        };
    }
}
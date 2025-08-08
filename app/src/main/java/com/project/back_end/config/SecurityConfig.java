package com.project.back_end.config;

import com.project.back_end.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Minimal Spring Security configuration:
 *  - Stateless (JWT)
 *  - Registers JwtAuthenticationFilter
 *  - Permits auth + registration endpoints
 *  - Protects all other endpoints
 *  - Example role-based route protection included (can adjust as needed)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // No sessions (stateless)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable CSRF for API usage
                .csrf(csrf -> csrf.disable())
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/patients/register",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        ).permitAll()
                        // Example: Only admin can access admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Example: Only doctor can access doctor schedule (adjust)
                        .requestMatchers("/api/appointments/doctor/**").hasRole("DOCTOR")
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // Add our JWT filter before username/password auth
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder for when you add hashing (use in CoreService.validateAdmin etc.)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
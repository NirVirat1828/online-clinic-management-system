package com.project.back_end.config;

import com.project.back_end.services.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads Authorization: Bearer <token>, validates with TokenService,
 * builds an Authentication containing ROLE_<role>.
 *
 * If invalid token -> no authentication set; downstream endpoints requiring auth will 401 automatically.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        try {
            // Quick validation: parse claims
            Claims claims = tokenService.parseClaims(token);
            String role = claims.get("role", String.class);
            String subject = claims.getSubject();
            Long uid = claims.get("uid", Long.class);

            if (role != null && subject != null) {
                // Spring Security expects ROLE_ prefix for hasRole()
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                AbstractAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                // Attach details (optional)
                auth.setDetails(uid);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // Invalid token -> leave context empty (request to protected endpoint will 401)
        }

        filterChain.doFilter(request, response);
    }
}
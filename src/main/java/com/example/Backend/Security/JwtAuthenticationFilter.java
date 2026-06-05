package com.example.Backend.Security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Backend.multitenancy.tenant.TenantContext;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService
            userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ PUBLIC ROUTES
        String path =
                request.getServletPath();

        if (
        path.equals("/auth/login") ||
        path.equals("/auth/signup") ||
        path.equals("/auth/refresh") ||
        path.startsWith("/ws")
){

           try {

    filterChain.doFilter(
            request,
            response
    );

} finally {

    TenantContext.clear();
}

            return;
        }

        String token = null;

        // ✅ TRY COOKIE FIRST
        if (request.getCookies() != null) {

            for (Cookie c : request.getCookies()) {

                if (
                        "access_token".equals(
                                c.getName()
                        )
                ) {

                    token = c.getValue();

                    break;
                }
            }
        }

        // ✅ FALLBACK TO AUTH HEADER
        if (token == null) {

            String header =
                    request.getHeader(
                            "Authorization"
                    );

            if (
                    header != null &&
                    header.startsWith("Bearer ")
            ) {

                token =
                        header.substring(7);
            }
        }

        // ✅ VALIDATE TOKEN
        if (
                token != null &&
                jwtUtil.validateToken(token) &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null
        ) {

           String username =
        jwtUtil.extractUsername(token);

String tenant =
        jwtUtil.extractTenant(token);

TenantContext.setTenant(tenant);

logger.info("JWT username: {}", username);
logger.info("Tenant from JWT: {}", tenant);

UserDetails userDetails =
        userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken
                    auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            auth.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);
            logger.info("JWT authentication success: {}", username);
        } else if (token != null && !jwtUtil.validateToken(token)) {
            logger.warn("Invalid JWT received for path: {}", path);
        }

        // ✅ CONTINUE FILTER CHAIN
        try {

    filterChain.doFilter(
            request,
            response
    );

} finally {

    TenantContext.clear();
}
    }
}

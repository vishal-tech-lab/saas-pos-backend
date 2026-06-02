package com.example.Backend.multitenancy.filter;

import com.example.Backend.multitenancy.tenant.TenantContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(TenantFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ PUBLIC ROUTES (no tenant required)
       if (
        path.equals("/")
        || path.startsWith("/tenant/create")
        || path.startsWith("/tenant/login")
        || path.startsWith("/auth/login")
        || path.startsWith("/auth/signup")
) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // ✅ REFRESH ENDPOINT - STILL NEEDS TENANT CONTEXT
        if (path.startsWith("/auth/refresh")) {
            String tenant = request.getHeader("X-Tenant-ID");
            if (tenant != null && !tenant.isBlank()) {
                TenantContext.setTenant(tenant);
                logger.info("Tenant resolved for refresh: {}", tenant);
            }
            try {
                filterChain.doFilter(request, response);
            } finally {
                TenantContext.clear();
            }
            return;
        }

        String tenant =
                request.getHeader(
                        "X-Tenant-ID"
                );

        if (
                tenant == null ||
                tenant.isBlank()
        ) {

            logger.warn(
                    "Tenant resolution failed, missing X-Tenant-ID for path: {}",
                    request.getServletPath()
            );

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "X-Tenant-ID header is required"
            );

            return;
        }

        TenantContext.setTenant(tenant);

        logger.info(
                "Tenant resolved: {}",
                tenant
        );

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
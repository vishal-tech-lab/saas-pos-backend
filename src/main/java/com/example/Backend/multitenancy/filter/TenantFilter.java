package com.example.Backend.multitenancy.filter;

import com.example.Backend.Entity.Tenant;
import com.example.Backend.Repository.TenantRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(TenantFilter.class);

    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
logger.info(
        "TenantFilter Executed -> {}",
        request.getRequestURI()
);
        String path = request.getServletPath();

        // PUBLIC ROUTES
        if (
                path.equals("/")
                  
                        || path.startsWith("/tenant/create")
                        || path.startsWith("/tenant/login")
                          || path.startsWith("/tenant/onboard")
|| path.startsWith("/tenant/by-subdomain")
                        || path.startsWith("/ws")
        ) {

            filterChain.doFilter(request, response);
            return;
        }

        String subdomain = request.getHeader(
                "X-Tenant-ID"
        );

        logger.info(
                "Tenant Header: {}",
                subdomain
        );

        if (
                subdomain == null ||
                subdomain.isBlank()
        ) {
            throw new RuntimeException(
                    "Tenant header missing"
            );
        }

        logger.info("Subdomain: {}", subdomain);

        Tenant tenantEntity =
                tenantRepository
                        .findBySubdomain(subdomain)
                        .orElseThrow(() -> new RuntimeException(
                                "Tenant not found for subdomain: "
                                        + subdomain
                        ));

        String tenantSchema =
                tenantEntity.getSchemaName();

        logger.info(
                "Schema: {}",
                tenantSchema
        );
        TenantContext.setTenant(
                tenantSchema
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
package com.example.Backend.multitenancy.filter;

import com.example.Backend.multitenancy.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String tenant = request.getHeader("X-Tenant-ID");

        System.out.println("HEADER TENANT : " + tenant);

        if (tenant != null && !tenant.isEmpty()) {

            TenantContext.setTenant(tenant);

        } else {

            TenantContext.setTenant("public");
        }

        try {

            filterChain.doFilter(request, response);

        } finally {

            TenantContext.clear();
        }
    }
}
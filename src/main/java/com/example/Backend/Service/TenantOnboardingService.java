package com.example.Backend.Service;

import org.springframework.stereotype.Service;

import com.example.Backend.Dto.TenantOnboardingRequest;
import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.multitenancy.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantOnboardingService {

    private final TenantProvisionService tenantProvisionService;
    private final TenantDataInitializer tenantDataInitializer;

    public String onboardTenant(
            TenantOnboardingRequest request
    ) {

        validateRequest(request);

        TenantRequest tenantRequest = new TenantRequest();

        tenantRequest.setCompanyName(
                request.getCompanyName()
        );

        tenantRequest.setSubdomain(
                request.getSubdomain()
        );

        tenantRequest.setPlan(
                request.getPlan()
        );

        tenantRequest.setUsername(
                request.getAdminUsername()
        );

        tenantRequest.setPassword(
                request.getAdminPassword()
        );

        String result =
                tenantProvisionService.createTenant(
                        tenantRequest
                );

        if (result == null
                || result.startsWith("ERROR")
                || result.contains("ALREADY")) {

            throw new RuntimeException(result);
        }

        String schemaName;

        if (request.getSubdomain() != null
                && !request.getSubdomain().isBlank()) {

            schemaName =
                    "tenant_"
                            + request.getSubdomain()
                                    .toLowerCase()
                                    .trim()
                                    .replace("-", "_");

        } else {

            schemaName =
                    "tenant_"
                            + request.getCompanyName()
                                    .toLowerCase()
                                    .trim()
                                    .replace(" ", "_")
                                    .replace("-", "_");
        }

        TenantContext.setTenant(schemaName);

        try {

            tenantDataInitializer.initialize(
                    schemaName,
                    request
            );

        } finally {

            TenantContext.clear();
        }

        return "Tenant onboarded successfully";
    }

    private void validateRequest(
            TenantOnboardingRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }

        if (isBlank(request.getCompanyName())) {
            throw new IllegalArgumentException(
                    "Company name is required"
            );
        }

        if (isBlank(request.getBranchName())) {
            throw new IllegalArgumentException(
                    "Branch name is required"
            );
        }

        if (isBlank(request.getAdminUsername())
                || isBlank(request.getAdminPassword())) {

            throw new IllegalArgumentException(
                    "Admin credentials are required"
            );
        }

        if (isBlank(request.getManagerUsername())
                || isBlank(request.getManagerPassword())) {

            throw new IllegalArgumentException(
                    "Manager credentials are required"
            );
        }

        if (isBlank(request.getCashierUsername())
                || isBlank(request.getCashierPassword())) {

            throw new IllegalArgumentException(
                    "Cashier credentials are required"
            );
        }
    }

    private boolean isBlank(
            String value
    ) {

        return value == null
                || value.isBlank();
    }
}
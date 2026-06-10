package com.example.Backend.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Backend.Dto.TenantOnboardingRequest;
import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.User;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.UserRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantOnboardingService {

    private final TenantProvisionService tenantProvisionService;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public String onboardTenant(
            TenantOnboardingRequest request
    ) {
        validateRequest(request);

        TenantRequest tenantRequest = buildTenantRequest(request);
        String creationResult = tenantProvisionService.createTenant(tenantRequest);
        if (creationResult == null || creationResult.startsWith("ERROR")) {
            throw new RuntimeException(
                    "Tenant provisioning failed: " + creationResult);
        }

        String schemaName = normalizeSchemaName(request.getSubdomain(), request.getCompanyName());
        try {

            TenantContext.setTenant(schemaName);

            Branch branch =
                    buildBranch(
                            request.getBranchName()
                    );

            System.out.println("STEP 1");

            Branch savedBranch =
                    branchRepository.save(branch);

            System.out.println("STEP 2");

            createUser(
                    request.getManagerUsername(),
                    request.getManagerPassword(),
                    "ROLE_MANAGER",
                    "APPROVED",
                    savedBranch
            );

            System.out.println("STEP 3");

            createUser(
                    request.getCashierUsername(),
                    request.getCashierPassword(),
                    "ROLE_CASHIER",
                    "APPROVED",
                    savedBranch
            );

            System.out.println("STEP 4");

        } finally {

            TenantContext.clear();
        }

        return "Tenant onboarded successfully";
    }

    private void validateRequest(TenantOnboardingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Tenant onboarding request cannot be null");
        }
        if (isBlank(request.getCompanyName())) {
            throw new IllegalArgumentException("companyName is required");
        }
        if (isBlank(request.getAdminUsername()) || isBlank(request.getAdminPassword())) {
            throw new IllegalArgumentException("Admin credentials are required");
        }
        if (isBlank(request.getManagerUsername()) || isBlank(request.getManagerPassword())) {
            throw new IllegalArgumentException("Manager credentials are required");
        }
        if (isBlank(request.getCashierUsername()) || isBlank(request.getCashierPassword())) {
            throw new IllegalArgumentException("Cashier credentials are required");
        }
        if (isBlank(request.getBranchName())) {
            throw new IllegalArgumentException("branchName is required");
        }
    }

    private TenantRequest buildTenantRequest(TenantOnboardingRequest request) {
        TenantRequest tenantRequest = new TenantRequest();
        tenantRequest.setCompanyName(request.getCompanyName());
        tenantRequest.setSubdomain(request.getSubdomain());
        tenantRequest.setPlan(request.getPlan());
        tenantRequest.setUsername(request.getAdminUsername());
        tenantRequest.setPassword(request.getAdminPassword());
        return tenantRequest;
    }

    private Branch buildBranch(String branchName) {
        Branch branch = new Branch();
        branch.setBranchname(branchName);
        branch.setBranchtype("HEAD");
        branch.setAddress("Head Office");
        branch.setPhone("0000000000");
        branch.setStatus("ACTIVE");
        return branch;
    }

    private void createUser(
            String username,
            String password,
            String role,
            String status,
            Branch branch
    ) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("User already exists: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(status);
        user.setBranch(branch);
        userRepository.save(user);
    }

    private String normalizeSchemaName(String subdomain, String companyName) {
        if (isBlank(subdomain)) {
            subdomain = companyName;
        }
        return "tenant_" + subdomain
                .toLowerCase()
                .trim()
                .replace(" ", "_")
                .replace("-", "_");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

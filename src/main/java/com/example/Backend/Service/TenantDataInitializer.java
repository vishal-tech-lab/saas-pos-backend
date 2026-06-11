package com.example.Backend.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import com.example.Backend.Dto.TenantOnboardingRequest;
import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.User;
import com.example.Backend.Repository.BranchRepository;
import com.example.Backend.Repository.UserRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantDataInitializer {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void initialize(
            String schemaName,
            TenantOnboardingRequest request
    ) {

        TenantContext.setTenant(schemaName);


System.out.println(
    "SET TENANT = " +
    TenantContext.getTenant()
);
        try {

            Branch branch = new Branch();
            branch.setBranchname(request.getBranchName());
            branch.setBranchtype("HEAD");
            branch.setAddress("Head Office");
            branch.setPhone("0000000000");
            branch.setStatus("ACTIVE");

            Branch savedBranch = branchRepository.save(branch);

            User manager = new User();
            manager.setUsername(request.getManagerUsername());
            manager.setPassword(passwordEncoder.encode(request.getManagerPassword()));
            manager.setRole("ROLE_MANAGER");
            manager.setStatus("APPROVED");
            manager.setBranch(savedBranch);

            userRepository.save(manager);

            User cashier = new User();
            cashier.setUsername(request.getCashierUsername());
            cashier.setPassword(passwordEncoder.encode(request.getCashierPassword()));
            cashier.setRole("ROLE_CASHIER");
            cashier.setStatus("APPROVED");
            cashier.setBranch(savedBranch);

            userRepository.save(cashier);

        } finally {
            TenantContext.clear();
        }
    }
}
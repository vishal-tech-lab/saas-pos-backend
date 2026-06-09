package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.Entity.Tenant;
import com.example.Backend.Repository.TenantRepository;
import com.example.Backend.Service.TenantProvisionService;
import com.example.Backend.Dto.TenantLoginRequest;
import com.example.Backend.Dto.TenantLoginResponse;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantProvisionService
            tenantProvisionService;

            @Autowired
private TenantRepository tenantRepository;

@GetMapping("/by-subdomain/{subdomain}")
public ResponseEntity<?> getTenantBySubdomain(

        @PathVariable
        String subdomain
) {

    Tenant tenant =
            tenantRepository
                    .findBySubdomain(
                            subdomain
                    )
                    .orElse(null);

    if (tenant == null) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message",
                                "Tenant not found"
                        )
                );
    }

    return ResponseEntity.ok(
            tenant
    );
}
    @PostMapping("/create")
    public String createTenant(

            @RequestBody
            TenantRequest request
    ) {

        return tenantProvisionService
                .createTenant(request);
    }
    @PostMapping("/login")
public TenantLoginResponse login(

        @RequestBody
        TenantLoginRequest request
) {

    return tenantProvisionService.login(

            request.getUsername(),
            request.getPassword()
    );
}
}
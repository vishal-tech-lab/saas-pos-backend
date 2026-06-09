package com.example.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.Dto.TenantOnboardingRequest;
import com.example.Backend.Service.TenantOnboardingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
public class TenantOnboardingController {

    private final TenantOnboardingService tenantOnboardingService;

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardTenant(
            @RequestBody TenantOnboardingRequest request
    ) {
        try {
            String result = tenantOnboardingService.onboardTenant(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of(
                            "message",
                            e.getMessage()
                    )
            );
        }
    }
}

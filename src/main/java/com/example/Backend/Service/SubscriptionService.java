package com.example.Backend.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Backend.Dto.SubscriptionDto;
import com.example.Backend.Entity.Tenant;
import com.example.Backend.Repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final TenantRepository tenantRepository;

    public List<SubscriptionDto> getAllSubscriptions() {
        return tenantRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public SubscriptionDto getSubscription(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Tenant not found with id: " + tenantId));

        return mapToDto(tenant);
    }

    public SubscriptionDto renewSubscription(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Tenant not found with id: " + tenantId));

        tenant.setSubscriptionStartDate(LocalDate.now());
        tenant.setSubscriptionEndDate(LocalDate.now().plusDays(30));
        tenant.setSubscriptionStatus("ACTIVE");

        Tenant updatedTenant = tenantRepository.save(tenant);
        return mapToDto(updatedTenant);
    }

    private SubscriptionDto mapToDto(Tenant tenant) {
        return new SubscriptionDto(
                tenant.getId(),
                tenant.getCompanyName(),
                tenant.getSubdomain(),
                tenant.getPlan(),
                tenant.getSubscriptionStatus(),
                tenant.getSubscriptionStartDate(),
                tenant.getSubscriptionEndDate()
        );
    }
}

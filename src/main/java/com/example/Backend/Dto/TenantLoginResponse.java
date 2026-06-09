package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TenantLoginResponse {

    private String tenantId;
    private String username;
    private String role;

    private String plan;

    private String subscriptionStatus;
}
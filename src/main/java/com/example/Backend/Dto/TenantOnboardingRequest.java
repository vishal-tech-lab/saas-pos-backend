package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantOnboardingRequest {

    private String companyName;
    private String subdomain;
    private String plan;

    private String branchName;

    private String adminUsername;
    private String adminPassword;

    private String managerUsername;
    private String managerPassword;

    private String cashierUsername;
    private String cashierPassword;
}

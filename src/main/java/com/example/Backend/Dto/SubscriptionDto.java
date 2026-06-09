package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

    private Long tenantId;
    private String companyName;
    private String subdomain;
    private String plan;
    private String subscriptionStatus;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
}

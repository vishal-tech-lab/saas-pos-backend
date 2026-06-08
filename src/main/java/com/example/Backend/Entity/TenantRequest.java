package com.example.Backend.Entity;

import lombok.Data;

@Data
public class TenantRequest {

    private String companyName;

    private String subdomain;

    private String username;

    private String password;
}
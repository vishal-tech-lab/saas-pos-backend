package com.example.Backend.Dto;

import lombok.Data;

@Data
public class TenantRequest {

    private String companyName;

    private String username;

    private String password;
}
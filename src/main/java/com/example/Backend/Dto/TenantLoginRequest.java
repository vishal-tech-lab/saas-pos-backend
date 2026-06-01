package com.example.Backend.Dto;

import lombok.Data;

@Data
public class TenantLoginRequest {

    private String username;
    private String password;
}
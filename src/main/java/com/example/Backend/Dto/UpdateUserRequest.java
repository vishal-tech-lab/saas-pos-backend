package com.example.Backend.Dto;

import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role;

    private String status;

    private Long branchId;
}

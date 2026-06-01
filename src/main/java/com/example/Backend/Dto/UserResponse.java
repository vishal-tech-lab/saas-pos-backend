package com.example.Backend.Dto;

import lombok.Data;

@Data
public class UserResponse {

    private Long id;
    private String username;
    private String role;
    private String status;
    private Long branchId;
    private String branchName;
}

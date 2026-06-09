package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String username;
    private String role;
    private String status;
    private String schema;
    private Long branchid;
    private String branchname;
    private String branchtype;
    private String plan;

private String subscriptionStatus;
}

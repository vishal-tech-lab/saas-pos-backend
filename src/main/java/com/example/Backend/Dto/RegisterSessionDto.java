package com.example.Backend.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSessionDto {

    private Long sessionid;

    private Long branchid;

    private Boolean active;

    private Double totalSales;

    private Integer totalBills;
}
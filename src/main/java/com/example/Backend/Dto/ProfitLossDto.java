package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLossDto {

    private Double totalSales;

    private Double totalExpenses;

    private Double netProfit;
}

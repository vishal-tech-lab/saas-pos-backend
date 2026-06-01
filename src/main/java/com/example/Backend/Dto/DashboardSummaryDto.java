package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private Double todaySales;

    private Long todayOrders;

    private Double todayProfit;

    private Long lowStockItems;
}
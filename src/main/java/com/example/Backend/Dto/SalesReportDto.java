package com.example.Backend.Dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDto {

   private Double totalSales;

private Integer totalBills;

private Double cashSales;

private Double upiSales;

private List<SalesReportItemDto> items;}
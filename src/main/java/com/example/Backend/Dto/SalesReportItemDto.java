package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportItemDto {

private String itemname;

private String category;

private Double qty;

private Double total;
}
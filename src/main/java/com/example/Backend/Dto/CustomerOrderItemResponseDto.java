package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItemResponseDto {
    
    private Long id;
    
    private String productName;
    
    private Double qty;
    
    private Double price;
    
    private Double total;
}

package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerOrderDto {
    
    private Long tableId;
    
    private List<CustomerOrderItemDto> items;
}

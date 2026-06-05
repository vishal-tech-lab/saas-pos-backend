package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderResponseDto {
    
    private Long orderId;
    
    private String tableName;
    
    private Double totalAmount;
    
    private String paymentStatus;
    
    private String orderStatus;
    
    private LocalDateTime createdAt;
    
    private List<CustomerOrderItemResponseDto> items;
}

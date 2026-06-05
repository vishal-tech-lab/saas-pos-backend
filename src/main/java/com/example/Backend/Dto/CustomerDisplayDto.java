package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * CustomerDisplayDto
 * DTO for customer display
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDisplayDto {

    private Long branchid;
    private String billno;
    private Double total;
    private String status;
    private List<CustomerDisplayItemDto> items;
}

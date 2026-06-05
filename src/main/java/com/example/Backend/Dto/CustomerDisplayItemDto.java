package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * CustomerDisplayItemDto
 * DTO for individual items in customer display
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDisplayItemDto {

    private String itemname;
    private Double qty;
    private Double price;
    private Double total;
}

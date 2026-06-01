package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KitchenProductionDto {

    private String branchname;
    private String productname;
    private Double qty;
    private String notes;
}

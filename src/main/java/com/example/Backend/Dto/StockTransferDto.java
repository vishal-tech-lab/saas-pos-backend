package com.example.Backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferDto {

    private String frombranch;
    private String tobranch;
    private String productname;
    private Double qty;
}

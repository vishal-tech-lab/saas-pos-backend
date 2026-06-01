package com.example.Backend.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesItemDto {

    @JsonProperty("branchname")
    private String branchName;

    @JsonProperty("productname")
    private String productName;

    private Double qty;
    private String paymentmethod;
    private String billno;
}

package com.example.Backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpiPaymentRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    private Long amount; // amount in rupees

    @NotBlank(message = "UPI ID is required")
    private String upiId; // e.g., user@paytm

    private String vpa; // Virtual Payment Address

    private String description;

    private String customerId;

    private String orderId;
}

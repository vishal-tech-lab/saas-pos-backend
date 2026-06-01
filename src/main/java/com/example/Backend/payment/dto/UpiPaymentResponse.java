package com.example.Backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpiPaymentResponse {
    private String orderId;
    private Long amount;
    private String currency;
    private String upiId;
    private String paymentId;
    private String status;
    private String key;
}

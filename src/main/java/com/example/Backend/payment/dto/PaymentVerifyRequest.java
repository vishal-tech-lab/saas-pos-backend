package com.example.Backend.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest {

    @NotBlank
    private String razorpay_order_id;

    @NotBlank
    private String razorpay_payment_id;

    @NotBlank
    private String razorpay_signature;
}

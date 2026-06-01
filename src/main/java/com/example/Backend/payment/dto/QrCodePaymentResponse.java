package com.example.Backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodePaymentResponse {
    private String orderId;
    private Long amount;
    private String currency;
    private String qrCodeUrl; // URL to the generated QR code image
    private String qrCodeData; // Raw QR code data
    private String paymentId;
    private String status;
    private String key;
}

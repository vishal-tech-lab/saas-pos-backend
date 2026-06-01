package com.example.Backend.payment;

import com.example.Backend.payment.dto.CreateOrderRequest;
import com.example.Backend.payment.dto.CreateOrderResponse;
import com.example.Backend.payment.dto.PaymentVerifyRequest;
import com.example.Backend.payment.dto.UpiPaymentRequest;
import com.example.Backend.payment.dto.UpiPaymentResponse;
import com.example.Backend.payment.dto.QrCodePaymentRequest;
import com.example.Backend.payment.dto.QrCodePaymentResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.Map;

import com.example.Backend.multitenancy.tenant.TenantContext;

@RestController
@RequestMapping("/payment")
@Validated
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("Received create-order request from tenant {} for amount {}", TenantContext.getTenant(), request.getAmount());
        CreateOrderResponse response = paymentService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@Valid @RequestBody PaymentVerifyRequest verifyRequest) {
        try {
            logger.info("Verifying payment for tenant {} order {}", TenantContext.getTenant(), verifyRequest.getRazorpay_order_id());

            String payload = verifyRequest.getRazorpay_order_id() + "|" + verifyRequest.getRazorpay_payment_id();

            // compute HMAC SHA256 using secret from properties
            String computed = HmacUtil.hmacSha256(payload, keySecret);
            boolean valid = computed.equals(verifyRequest.getRazorpay_signature());

            if (valid) {
                logger.info("Payment verification success for order {}", verifyRequest.getRazorpay_order_id());
                return ResponseEntity.ok(Map.of("status", "success"));
            } else {
                logger.warn("Payment verification failed for order {}", verifyRequest.getRazorpay_order_id());
                return ResponseEntity.ok(Map.of("status", "failed"));
            }
        } catch (Exception e) {
            logger.error("Error during payment verification", e);
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/upi/create")
    public ResponseEntity<UpiPaymentResponse> createUpiPayment(@Valid @RequestBody UpiPaymentRequest request) {
        logger.info("Received UPI payment request from tenant {} for amount {}", TenantContext.getTenant(), request.getAmount());
        UpiPaymentResponse response = paymentService.createUpiPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/qr-code/create")
    public ResponseEntity<QrCodePaymentResponse> createQrCodePayment(@Valid @RequestBody QrCodePaymentRequest request) {
        logger.info("Received QR Code payment request from tenant {} for amount {}", TenantContext.getTenant(), request.getAmount());
        QrCodePaymentResponse response = paymentService.createQrCodePayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/methods")
    public ResponseEntity<Map<String, Object>> getSupportedPaymentMethods() {
        logger.info("Fetching supported payment methods for tenant {}", TenantContext.getTenant());
        Map<String, Object> methods = Map.of(
            "methods", new String[]{"card", "netbanking", "wallet", "upi", "qr_code"},
            "key", keyId
        );
        return ResponseEntity.ok(methods);
    }
}

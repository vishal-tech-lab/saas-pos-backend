package com.example.Backend.payment;

import com.example.Backend.payment.dto.CreateOrderRequest;
import com.example.Backend.payment.dto.CreateOrderResponse;
import com.example.Backend.payment.dto.QrCodePaymentRequest;
import com.example.Backend.payment.dto.QrCodePaymentResponse;
import com.example.Backend.payment.dto.UpiPaymentRequest;
import com.example.Backend.payment.dto.UpiPaymentResponse;
import com.example.Backend.payment.enums.PaymentMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.Backend.multitenancy.tenant.TenantContext;

import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final String keyId;
    private final String keySecret;
    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentService(
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret
    ) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        logger.info("PaymentService initialized for key id: {}", keyId);
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        try {
            long amountInPaise = request.getAmount() * 100L;

            Map<String, Object> payload = new HashMap<>();
            payload.put("amount", amountInPaise);
            payload.put("currency", "INR");
            payload.put("receipt", request.getOrderId() != null ? request.getOrderId() : "rcpt_" + System.currentTimeMillis());

            // Add payment method if specified
            if (request.getPaymentMethod() != null && !request.getPaymentMethod().isBlank()) {
                try {
                    PaymentMethod method = PaymentMethod.fromValue(request.getPaymentMethod());
                    payload.put("method", method.getValue());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid payment method: {}, proceeding without method restriction", request.getPaymentMethod());
                }
            }

            if (request.getDescription() != null) {
                payload.put("description", request.getDescription());
            }

            if (request.getCustomerId() != null) {
                payload.put("customer_id", request.getCustomerId());
            }

            logger.info("Creating Razorpay order for tenant {} amount {} paise method {}", 
                TenantContext.getTenant(), amountInPaise, request.getPaymentMethod());

            String url = "https://api.razorpay.com/v1/orders";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = keyId + ":" + keySecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> resp = restTemplate.postForEntity(new URI(url), entity, Map.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map body = resp.getBody();
                Object id = body.get("id");
                Object currency = body.get("currency");
                logger.info("Razorpay order created: {}", id);

                return new CreateOrderResponse(
                        id != null ? id.toString() : null,
                        request.getAmount(),
                        currency != null ? currency.toString() : "INR",
                        keyId
                );
            }

            logger.error("Failed to create Razorpay order, status: {}", resp.getStatusCode());
            throw new RuntimeException("Failed to create order: " + resp.getStatusCode());

        } catch (Exception e) {
            logger.error("Failed to create Razorpay order", e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    public UpiPaymentResponse createUpiPayment(UpiPaymentRequest request) {
        try {
            long amountInPaise = request.getAmount() * 100L;
            String orderId = request.getOrderId() != null ? request.getOrderId() : "upi_" + UUID.randomUUID();

            Map<String, Object> payload = new HashMap<>();
            payload.put("amount", amountInPaise);
            payload.put("currency", "INR");
            payload.put("receipt", orderId);
            payload.put("method", "upi");

            if (request.getDescription() != null) {
                payload.put("description", request.getDescription());
            }

            if (request.getCustomerId() != null) {
                payload.put("customer_id", request.getCustomerId());
            }

            // VPA (Virtual Payment Address) for UPI
            Map<String, Object> upiDetails = new HashMap<>();
            upiDetails.put("vpa", request.getVpa() != null ? request.getVpa() : request.getUpiId());
            payload.put("upi", upiDetails);

            logger.info("Creating Razorpay UPI payment for tenant {} amount {} paise UPI {}", 
                TenantContext.getTenant(), amountInPaise, request.getUpiId());

            String url = "https://api.razorpay.com/v1/orders";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = keyId + ":" + keySecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(new URI(url), entity, Map.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map body = resp.getBody();
                Object id = body.get("id");
                Object currency = body.get("currency");
                logger.info("Razorpay UPI order created: {}", id);

                return new UpiPaymentResponse(
                        id != null ? id.toString() : orderId,
                        request.getAmount(),
                        currency != null ? currency.toString() : "INR",
                        request.getUpiId(),
                        null,
                        "created",
                        keyId
                );
            }

            logger.error("Failed to create Razorpay UPI payment, status: {}", resp.getStatusCode());
            throw new RuntimeException("Failed to create UPI payment: " + resp.getStatusCode());

        } catch (Exception e) {
            logger.error("Failed to create Razorpay UPI payment", e);
            throw new RuntimeException("Failed to create UPI payment: " + e.getMessage());
        }
    }

    public QrCodePaymentResponse createQrCodePayment(QrCodePaymentRequest request) {
        try {
            long amountInPaise = request.getAmount() * 100L;
            String orderId = request.getOrderId() != null ? request.getOrderId() : "qr_" + UUID.randomUUID();

            Map<String, Object> payload = new HashMap<>();
            payload.put("amount", amountInPaise);
            payload.put("currency", "INR");
            payload.put("receipt", orderId);
            payload.put("method", "emandate");

            if (request.getDescription() != null) {
                payload.put("description", request.getDescription());
            }

            if (request.getCustomerId() != null) {
                payload.put("customer_id", request.getCustomerId());
            }

            logger.info("Creating Razorpay QR Code payment for tenant {} amount {} paise", 
                TenantContext.getTenant(), amountInPaise);

            // Create QR code order using Razorpay QR API
            String url = "https://api.razorpay.com/v1/payments/createJsonPayment";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = keyId + ":" + keySecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> resp = restTemplate.postForEntity(new URI(url), entity, Map.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Map body = resp.getBody();
                Object id = body.get("id");
                Object currency = body.get("currency");
                logger.info("Razorpay QR Code payment created: {}", id);

                // Generate QR code URL (this would typically come from Razorpay)
                String qrCodeUrl = generateQrCodeUrl(id != null ? id.toString() : orderId, request.getAmount());

                return new QrCodePaymentResponse(
                        id != null ? id.toString() : orderId,
                        request.getAmount(),
                        currency != null ? currency.toString() : "INR",
                        qrCodeUrl,
                        null,
                        null,
                        "created",
                        keyId
                );
            }

            logger.error("Failed to create Razorpay QR Code payment, status: {}", resp.getStatusCode());
            throw new RuntimeException("Failed to create QR Code payment: " + resp.getStatusCode());

        } catch (Exception e) {
            logger.error("Failed to create Razorpay QR Code payment", e);
            throw new RuntimeException("Failed to create QR Code payment: " + e.getMessage());
        }
    }

    private String generateQrCodeUrl(String orderId, Long amount) {
        // This generates a URL to a QR code. You can use a QR code service like:
        // - https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=...
        // - Or use a library like zxing to generate QR codes
        return String.format("https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=%s", 
            String.format("upi://pay?pa=%s&am=%d", orderId, amount));
    }
}

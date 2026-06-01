package com.example.Backend.payment.enums;

public enum PaymentMethod {
    CARD("card"),
    NETBANKING("netbanking"),
    WALLET("wallet"),
    UPI("upi"),
    QR_CODE("qr_code");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method: " + value);
    }
}

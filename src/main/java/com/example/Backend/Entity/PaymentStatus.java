package com.example.Backend.Entity;

public enum PaymentStatus {
    PENDING("PENDING", "Payment awaiting"),
    COMPLETED("COMPLETED", "Payment completed"),
    CANCELLED("CANCELLED", "Payment cancelled"),
    REFUNDED("REFUNDED", "Payment refunded");

    private final String value;
    private final String description;

    PaymentStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + value);
    }
}

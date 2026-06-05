package com.example.Backend.Entity;

public enum OrderStatus {
    PENDING("PENDING", "Order received, awaiting confirmation"),
    CONFIRMED("CONFIRMED", "Order confirmed by kitchen"),
    PREPARING("PREPARING", "Kitchen is preparing the order"),
    READY("READY", "Order ready for pickup/serving"),
    SERVED("SERVED", "Order served to customer"),
    CANCELLED("CANCELLED", "Order cancelled");

    private final String value;
    private final String description;

    OrderStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }
}

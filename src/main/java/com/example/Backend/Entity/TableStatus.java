package com.example.Backend.Entity;

public enum TableStatus {
    ACTIVE("ACTIVE", "Table available for use"),
    INACTIVE("INACTIVE", "Table not available"),
    RESERVED("RESERVED", "Table reserved"),
    OCCUPIED("OCCUPIED", "Table currently occupied");

    private final String value;
    private final String description;

    TableStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static TableStatus fromValue(String value) {
        for (TableStatus status : TableStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid table status: " + value);
    }
}

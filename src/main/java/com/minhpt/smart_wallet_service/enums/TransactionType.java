package com.minhpt.smart_wallet_service.enums;

public enum TransactionType {
    INCOME, EXPENSE;

    public static TransactionType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction type must not be blank");
        }
        try {
            return TransactionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + value);
        }
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            TransactionType.valueOf(value.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

package com.sharvari.expensemanager.model;

public enum PaymentMode {

    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    UPI,
    NET_BANKING,
    OTHER;

    public static PaymentMode fromString(String input) {
        try {
            return PaymentMode.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}

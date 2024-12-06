package com.fit.paymentservice.enums;

public enum PaymentMethod {
    PAYPAL(0),
    CREDIT_CARD(1),
    DEBIT_CARD(2),
    BANK_TRANSFER(3),
    CASH(4);

    private int value;

    PaymentMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


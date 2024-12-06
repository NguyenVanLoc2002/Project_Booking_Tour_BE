package com.fit.paymentservice.enums;

public enum PaymentStatus {
    PENDING(0),
    COMPLETED(1),
    FAILED(2),
    REFUNDED(3);

    private int value;

    PaymentStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

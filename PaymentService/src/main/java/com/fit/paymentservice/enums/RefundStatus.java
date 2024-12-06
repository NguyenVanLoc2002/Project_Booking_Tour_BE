package com.fit.paymentservice.enums;

public enum RefundStatus {
    PENDING(0),
    COMPLETED(1),
    FAILED(2);

    private int value;

    private RefundStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

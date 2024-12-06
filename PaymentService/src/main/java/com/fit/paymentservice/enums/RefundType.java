package com.fit.paymentservice.enums;

public enum RefundType {
    FULL(0),
    PARTIAL(1);

    private int value;

    private RefundType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
package com.fit.paymentservice.enums;

public enum Currency {
    USD(0),
    EUR(1),
    VND(2),
    JPY(3);

    private int value;

    Currency(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


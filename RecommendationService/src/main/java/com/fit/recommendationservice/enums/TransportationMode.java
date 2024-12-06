package com.fit.recommendationservice.enums;

public enum TransportationMode {
    AIRPLANE(0),
    BUS(1),
    TRAIN(2),
    PRIVATE_CAR(3);

    private int value;

    public int getValue() {
        return value;
    }

    TransportationMode(int value) {
        this.value = value;
    }

    public static TransportationMode fromValue(int value) {
        for (TransportationMode type : TransportationMode.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}


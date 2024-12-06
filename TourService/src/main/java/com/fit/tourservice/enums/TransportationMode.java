package com.fit.tourservice.enums;

public enum TransportationMode {
    AIRPLANE(0),
    BUS(1),
    TRAIN(2),
    PRIVATE_CAR(3),
    SHIP(4);

    private int value;

    public int getValue() {
        return value;
    }

    TransportationMode(int value) {
        this.value = value;
    }
}


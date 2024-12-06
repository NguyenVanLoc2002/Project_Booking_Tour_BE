package com.fit.tourservice.enums;

public enum WeatherCondition {
    SUNNY(0),
    RAINY(1),
    CLOUDY(2),
    SNOWY(3),
    STORMY(4),
    WINDY(5);

    private int value;

    public int getValue() {
        return value;
    }

    WeatherCondition(int value) {
        this.value = value;
    }
}

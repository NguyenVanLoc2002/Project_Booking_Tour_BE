package com.fit.recommendationservice.enums;

public enum TypeTour {
    CULTURE(0),
    ECOLOGY(1),
    RESORT(2),
    ENTERTAINMENT(3),
    SPORT(4),
    DISCOVER(5),
    VENTURE(6);

    private int value;

    public int getValue() {
        return value;
    }

    TypeTour(int value) {
        this.value = value;
    }

    public static TypeTour fromValue(int value) {
        for (TypeTour type : TypeTour.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

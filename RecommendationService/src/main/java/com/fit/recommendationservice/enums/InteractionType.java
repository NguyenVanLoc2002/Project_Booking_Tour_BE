package com.fit.recommendationservice.enums;

public enum InteractionType {
    VIEW(0),
    LIKE(1),
    SAVED(2),
    BOOK(3),
    REVIEW(4),
    SEARCH(5),
    SHARE(6),
    FILTER(7),
    INQUIRY(8);

    private int value;

    public int getValue() {
        return value;
    }

     InteractionType(int value) {
        this.value = value;
    }

    public static InteractionType fromValue(int value) {
        for (InteractionType type : InteractionType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

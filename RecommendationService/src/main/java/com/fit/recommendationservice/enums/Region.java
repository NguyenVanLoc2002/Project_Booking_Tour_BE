package com.fit.recommendationservice.enums;

public enum Region {
//    NORTH: Bắc
//    CENTRAL: Trung
//    SOUTH: Nam
//    CENTRAL_HIGHLANDS: Tây Nguyên
//    BEACH: Biển
//    MOUNTAIN: Núi
    NORTH(0),
    CENTRAL(1),
    SOUTH(2),
    CENTRAL_HIGHLANDS(3),
    BEACH(4),
    MOUNTAIN(5),
    WEST(6);

    private int value;

    public int getValue() {
        return value;
    }

    Region(int value) {
        this.value = value;
    }

    public static Region fromValue(int value) {
        for (Region type : Region.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

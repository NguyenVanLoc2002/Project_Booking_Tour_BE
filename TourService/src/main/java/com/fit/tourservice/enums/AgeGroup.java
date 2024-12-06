package com.fit.tourservice.enums;

public enum AgeGroup {
    ADULTS(0),//Over 11 years old
    CHILDREN(1),//5 - 11 years old
    NEWBORNS(2);//2-5 years old

    private int value;

    public int getValue() {
        return value;
    }

    AgeGroup(int value) {
        this.value = value;
    }
}

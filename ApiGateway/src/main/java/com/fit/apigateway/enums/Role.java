package com.fit.apigateway.enums;


public enum Role  {
    USER(0),
    ADMIN(1);

    private int value;

    public int getValue() {
        return value;
    }

    Role(int value) {
        this.value = value;
    }

}


package com.fit.authservice.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER(0),
    ADMIN(1);

    private int value;

    public int getValue() {
        return value;
    }

    Role(int value) {
        this.value = value;
    }

    @Override
    public String getAuthority() {
        return name(); // Trả về tên quyền
    }
}

package com.fit.tourservice.enums;

public enum SurchargeType {
    SERVICE_FEE(0),          // Phí dịch vụ
    TAX(1),                 // Thuế
    HANDLING_FEE(2),        // Phí xử lý
    INSURANCE(3),           // Bảo hiểm
    OTHER(4),               // Khác
    FOREIGN_SURCHARGE(5),   // Phụ thu Nước Ngoài
    SINGLE_ROOM_SUPPLEMENT(6); // Phụ thu Phòng đơn

    private int value;

    public int getValue() {
        return value;
    }

    SurchargeType(int value) {
        this.value = value;
    }
}

package com.fit.tourservice.enums;

public enum AccommodationQuality {
    //    HOMESTAY: Chỗ ở kiểu homestay.
//    THREE_STAR_HOTEL: Khách sạn 3 sao.
//            FOUR_STAR_HOTEL: Khách sạn 4 sao.
//            FIVE_STAR_HOTEL: Khách sạn 5 sao.
//            RESORT: Resort.
    HOMESTAY(0),
    THREE_STAR_HOTEL(1),
    FOUR_STAR_HOTEL(2),
    FIVE_STAR_HOTEL(3),
    RESORT(4);

    private int value;

    public int getValue() {
        return value;
    }

    AccommodationQuality(int value) {
        this.value = value;
    }
}

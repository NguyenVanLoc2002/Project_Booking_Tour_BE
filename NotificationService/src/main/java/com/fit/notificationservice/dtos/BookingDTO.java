package com.fit.notificationservice.dtos;

import com.fit.notificationservice.enums.StatusBooking;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String bookingId;
    private Long customerId;       // ID khách hàng nếu là khách đã đăng ký, null nếu là khách vãng lai
    private Long tourId;
    private Long ticketId;
    private LocalDate bookingDate;
    private StatusBooking statusBooking;
    private double totalAmount;
    private int quantity;
    private int adults;
    private int children;
    private int toddlers;
    private int infants;

    // Chỉ dùng cho khách vãng lai
    private String email;
    private String userName;
    private String phoneNumber;
    private String city;
    private String district;
    private String ward;
    private String address;
}


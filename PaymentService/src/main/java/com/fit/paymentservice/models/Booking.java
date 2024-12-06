package com.fit.paymentservice.models;

import com.fit.paymentservice.enums.StatusBooking;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "booking")
public class Booking {
    @Id
    private String bookingId;
    private Long tourId;
    private Long ticketId;
    private LocalDate bookingDate; // Ngày đặt tour
    private StatusBooking statusBooking; // Trạng thái booking
    private double totalAmount;    // Tổng số tiền
    private int quantity;          // Tổng số hành khách để cập nhật available slot
    private int adults;            // Số lượng người lớn
    private int children;          // Số lượng trẻ em (5 - 12 tuổi)
    private int toddlers;          // Số lượng trẻ nhỏ (2 - 5 tuổi)
    private int infants;           // Số lượng em bé (dưới 2 tuổi)

    // Chỉ dùng cho khách đã đăng ký
    private Long customerId;       // Chứa customerId nếu là khách đã đăng ký, null nếu là khách vãng lai


    // Chỉ dùng cho khách vãng lai
    private String email;          // Email của khách vãng lai
    private String userName;       // Tên người dùng vãng lai
    private String phoneNumber;    // Số điện thoại của khách vãng lai
    private String city;           // Tỉnh/Thành phố
    private String district;       // Quận/Huyện
    private String ward;           // Phường/Xã
    private String address;        // Địa chỉ cụ thể
}

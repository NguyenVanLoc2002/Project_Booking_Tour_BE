package com.fit.paymentservice.dtos.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    // Dùng chung cho cả khách đã đăng ký và khách vãng lai
    private String bookingId;
    private Long tourId;
    private Long ticketId;
    private int quantity;          // Tổng số hành khách để cập nhật available slot
    private int adults;            // Số lượng người lớn
    private int children;          // Số lượng trẻ em (5 - 12 tuổi)
    private int toddlers;          // Số lượng trẻ nhỏ (2 - 5 tuổi)
    private int infants;           // Số lượng em bé (dưới 2 tuổi)
    private double totalAmount;

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

    // Có thể thêm cờ để phân biệt giữa khách đã đăng ký và khách vãng lai
    public boolean isRegisteredCustomer() {
        return customerId != null; // Nếu customerId có giá trị, nghĩa là khách đã đăng ký
    }
}

package com.fit.paymentservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("discounts") // Tên bảng trong cơ sở dữ liệu
public class Discount {
    @Id
    private String discountId; // Discount ID

    private Long customerId; // ID người dùng

    private Long tourId; // ID tour liên quan

    private String description; // Mô tả giảm giá

    private String code; // Mã giảm giá

    private double amount; // Số tiền giảm giá

    private LocalDate startDate; // Ngày bắt đầu

    private LocalDate endDate; // Ngày kết thúc
}

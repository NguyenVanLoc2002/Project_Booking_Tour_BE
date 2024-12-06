package com.fit.paymentservice.models;

import com.fit.paymentservice.enums.RefundStatus;
import com.fit.paymentservice.enums.RefundType;
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
@Table("refunds") // Tên bảng trong cơ sở dữ liệu
public class Refund {
    @Id
    private String reId; // Refund ID

    private String paymentId; // ID của thanh toán liên quan

    private String transactionId; // ID của giao dịch hoàn tiền

    private LocalDate refundDate; // Ngày hoàn tiền

    private double amount; // Số tiền hoàn tiền

    private RefundStatus status; // Trạng thái hoàn tiền

    private RefundType typeRefund; // Loại hoàn tiền (FULL, PARTIAL)
}


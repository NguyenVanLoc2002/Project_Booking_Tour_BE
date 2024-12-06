package com.fit.paymentservice.dtos.response;

import com.fit.paymentservice.enums.RefundStatus;
import com.fit.paymentservice.enums.RefundType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefundResponseDTO {
    private String reId; // Refund ID
    private String paymentId; // ID của thanh toán liên quan
    private String transactionId; // ID của giao dịch hoàn tiền
    private String refundDate; // Ngày hoàn tiền (định dạng String cho dễ dàng sử dụng API)
    private double amount; // Số tiền hoàn tiền
    private RefundStatus status; // Trạng thái hoàn tiền
    private RefundType typeRefund; // Loại hoàn tiền (FULL, PARTIAL)
    private String message; // Thông báo lỗi nếu có

    // Constructor nhận thông báo lỗi
    public RefundResponseDTO(String message) {
        this.message = message;  // Gán thông báo lỗi vào đối tượng DTO
    }

}

package com.fit.paymentservice.dtos;

import com.fit.paymentservice.enums.Currency;
import com.fit.paymentservice.enums.PaymentMethod;
import com.fit.paymentservice.enums.PaymentStatus;
import com.fit.paymentservice.models.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String paymentId;
    private String bookingId;
    private String discountId; // Optional
    private double amount;
    private String paymentMethod; // Có thể chuyển thành chuỗi để dễ dàng truyền tải
    private String paymentStatus; // Có thể chuyển thành chuỗi để dễ dàng truyền tải
    private LocalDate paymentDate;
    private String currency; // Có thể chuyển thành chuỗi để dễ dàng truyền tải
    private String paymentReference; // PayPal hoặc tham chiếu của cổng thanh toán khác
    private double transactionFee; // Optional
    private LocalDate createdDate;
    private LocalDate updatedDate;

    // Phương thức chuyển đổi từ Payment sang PaymentDTO
    public static PaymentDTO convertToDTO(Payment payment) {
        if (payment == null) {
            return null; // hoặc có thể ném ra ngoại lệ
        }
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getDiscountId(),
                payment.getAmount(),
                payment.getPaymentMethod().name(), // Chuyển enum sang chuỗi
                payment.getPaymentStatus().name(), // Chuyển enum sang chuỗi
                payment.getPaymentDate(),
                payment.getCurrency().name(), // Chuyển enum sang chuỗi
                payment.getPaymentReference(),
                payment.getTransactionFee(),
                payment.getCreatedDate(),
                payment.getUpdatedDate()
        );
    }

    // Phương thức chuyển đổi từ PaymentDTO sang Payment
    public static Payment convertToEntity(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null; // hoặc có thể ném ra ngoại lệ
        }
        Payment payment = new Payment();
        payment.setPaymentId(paymentDTO.getPaymentId());
        payment.setBookingId(paymentDTO.getBookingId());
        payment.setDiscountId(paymentDTO.getDiscountId());
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(paymentDTO.getPaymentMethod())); // Chuyển chuỗi về enum
        payment.setPaymentStatus(PaymentStatus.valueOf(paymentDTO.getPaymentStatus())); // Chuyển chuỗi về enum
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setCurrency(Currency.valueOf(paymentDTO.getCurrency())); // Chuyển chuỗi về enum
        payment.setPaymentReference(paymentDTO.getPaymentReference());
        payment.setTransactionFee(paymentDTO.getTransactionFee());
        payment.setCreatedDate(paymentDTO.getCreatedDate());
        payment.setUpdatedDate(paymentDTO.getUpdatedDate());
        return payment;
    }
}

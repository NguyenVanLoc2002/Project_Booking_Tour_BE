package com.fit.paymentservice.models;

import com.fit.paymentservice.enums.Currency;
import com.fit.paymentservice.enums.PaymentMethod;
import com.fit.paymentservice.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@ToString
public class Payment {
    @Id
    private String paymentId;
    private String bookingId;
    private String discountId; // Optional, in case discounts apply
    private String transactionId;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private Currency currency;
    private String paymentReference; // PayPal or other gateway reference
    private double transactionFee; // Optional, depends on the gateway
    private LocalDate createdDate;
    private LocalDate updatedDate;
}

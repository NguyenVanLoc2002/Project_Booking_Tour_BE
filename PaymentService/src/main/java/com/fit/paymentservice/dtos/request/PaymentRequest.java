package com.fit.paymentservice.dtos.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest {
    private String paymentId;
    private String payerId;
    private String bookingId;
    private String discountId;
    private double amount;
    private String transactionId;
}

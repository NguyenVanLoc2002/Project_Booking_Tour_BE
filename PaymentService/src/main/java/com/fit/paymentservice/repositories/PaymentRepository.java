package com.fit.paymentservice.repositories;

import com.fit.paymentservice.models.Payment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface PaymentRepository extends ReactiveCrudRepository<Payment, String> {
    Mono<Payment> findPaymentByBookingId(String bookingId);

    // Phương thức chèn bản ghi mới vào bảng payments
    @Query("INSERT INTO payments (payment_id, booking_id,transaction_id,discount_id, amount, payment_method, payment_status, payment_date, currency, payment_reference, transaction_fee, created_date, updated_date) " +
            "VALUES (:paymentId, :bookingId, :transactionId ,:discountId, :amount, :paymentMethod, :paymentStatus, :paymentDate, :currency, :paymentReference, :transactionFee, :createdDate, :updatedDate)")
    Mono<Void> insertPayment(String paymentId, String bookingId, String transactionId,  String discountId, double amount, String paymentMethod, String paymentStatus, LocalDate paymentDate, String currency, String paymentReference, double transactionFee, LocalDate createdDate, LocalDate updatedDate);

}

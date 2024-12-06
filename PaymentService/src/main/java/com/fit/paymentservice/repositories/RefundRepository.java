package com.fit.paymentservice.repositories;

import com.fit.paymentservice.models.Refund;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface RefundRepository extends ReactiveCrudRepository<Refund, String> {
    // Phương thức thêm Refund mới
    @Query("INSERT INTO refunds (re_id, payment_id, transaction_id, refund_date, amount, status, type_refund) " +
            "VALUES (:reId, :paymentId, :transactionId, :refundDate, :amount, :status, :typeRefund) " +
            "RETURNING *")
    Mono<Refund> saveRefund(@Param("reId") String reId,
                            @Param("paymentId") String paymentId,
                            @Param("transactionId") String transactionId,
                            @Param("refundDate") LocalDate refundDate,
                            @Param("amount") double amount,
                            @Param("status") String status,
                            @Param("typeRefund") String typeRefund);
}

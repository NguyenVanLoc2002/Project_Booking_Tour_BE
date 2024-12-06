package com.fit.paymentservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisExpirationListener implements MessageListener {

    @Autowired
    private TourServiceClient tourServiceClient;

    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        log.info("Redis key expired: {}", expiredKey);

        if (expiredKey.startsWith("customer:")) {
            // Lấy customerId từ expiredKey
            String[] parts = expiredKey.split(":");
            if (parts.length > 1) {
                String customerId = parts[1];
                log.info("Processing expired bookings for customerId: {}", customerId);

                // Lấy danh sách bookings của customer từ Redis
                redisService.getBookingsByCustomerId(customerId)
                        .flatMap(bookingDTO -> {
                            // Đảm bảo rằng tourServiceClient.refundSlotTourTicket được gọi với từng booking
                            return tourServiceClient.refundSlotTourTicket(bookingDTO.getTicketId(), bookingDTO.getQuantity())
                                    .doOnSuccess(success -> log.info("Successfully refunded tickets for booking {}", bookingDTO.getBookingId()))
                                    .doOnError(error -> log.error("Failed to refund tickets for booking {}: {}", bookingDTO.getBookingId(), error.getMessage()));
                        })
                        .doOnTerminate(() -> log.info("Finished processing expired bookings for customerId: {}", customerId))
                        .subscribe();  // Chạy bất đồng bộ
            }
        }
    }
}


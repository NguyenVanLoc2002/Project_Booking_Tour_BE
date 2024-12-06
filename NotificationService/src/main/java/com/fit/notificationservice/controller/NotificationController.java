package com.fit.notificationservice.controller;

import com.fit.commonservice.utils.Constant;
import com.fit.notificationservice.events.EventProducer;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final EventProducer eventProducer;
    private final Gson gson;

    public NotificationController(EventProducer eventProducer, @Qualifier("gson") Gson gson) {
        this.eventProducer = eventProducer;
        this.gson = gson;
    }

    @GetMapping("/verify-booking-tour")
    public Mono<ResponseEntity<String>> verifyTour(@RequestParam Long bookingId) {
        try {
//            BookingRequest bookingRequest = getBookingRequestFromToken(token); // Lấy BookingRequest từ token

            // Gửi thông tin bookingRequest lên topic thông qua EventProducer
            return eventProducer.send(Constant.VERIFY_BOOKING_TOUR_TOPIC, String.valueOf(bookingId), gson.toJson(bookingId))
                    .map(result -> ResponseEntity.ok("Tour booking verified successfully!"))
                    .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error verifying tour: " + e.getMessage())));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying tour: " + e.getMessage())); // Xử lý lỗi từ getBookingRequestFromToken
        }
    }

//    private BookingRequest getBookingRequestFromToken(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(SECRET_KEY) // Thay đổi secret key phù hợp
//                .parseClaimsJws(token)
//                .getBody();
//
//        return new BookingRequest(
//                claims.get("customerId", Long.class),
//                claims.get("tourId", Long.class),
//                claims.get("email", String.class),
//                claims.get("userName", String.class),
//                claims.get("quantity", Integer.class)
//        );
//    }


    }

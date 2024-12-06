package com.fit.paymentservice.controllers;


import com.fit.paymentservice.dtos.BookingDTO;
import com.fit.paymentservice.dtos.request.BookingRequest;
import com.fit.paymentservice.dtos.response.BookingTourResponse;
import com.fit.paymentservice.enums.StatusBooking;
import com.fit.paymentservice.services.BookingService;
import com.fit.paymentservice.services.RedisService;
import com.fit.paymentservice.services.TourServiceClient;
import com.fit.paymentservice.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/booking")
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final RedisService redisService;
    private final JwtUtils jwtUtils;
    private final TourServiceClient tourServiceClient;


    public BookingController(BookingService bookingService, RedisService redisService, JwtUtils jwtUtils, TourServiceClient tourServiceClient) {
        this.bookingService = bookingService;
        this.redisService = redisService;
        this.jwtUtils = jwtUtils;
        this.tourServiceClient = tourServiceClient;
    }


    @PostMapping("/bookTour")
    public Mono<ResponseEntity<BookingDTO>> bookTour(@RequestBody BookingRequest bookingRequest) {
        return bookingService.createBookingTour(bookingRequest)
                .flatMap(bookingResponse -> {
                    if (bookingResponse != null && bookingResponse.isAvailable()) {
                        BookingDTO bookingDTO = bookingService.mapBookingResponseToDTO(bookingRequest, bookingResponse);
                        return redisService.saveBookingTourFromRedis(bookingDTO)
                                .flatMap(saved -> {
                                    if (saved) {
                                        return bookingService.sendBookingNotification(bookingDTO)
                                                .then(Mono.just(ResponseEntity.ok(bookingDTO)));
                                    } else {
                                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body(new BookingDTO()));
                                    }
                                })
                                .onErrorResume(e -> {
                                    log.error("Failed to send Kafka message: {}", e.getMessage());
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                });
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BookingDTO()));
                    }
                })
                .onErrorResume(throwable -> {
                    log.error("Error occurred: {}", throwable.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    //Lay du lieu tu Redis
    @GetMapping("redis/{bookingId}")
    public Mono<ResponseEntity<BookingTourResponse>> getBookingTour(@PathVariable String bookingId) {

        return redisService.getDataAsBookingDTO(bookingId)
                .flatMap(bookingDTO ->
                        tourServiceClient.getTourByTicketId(bookingDTO.getTicketId())
                                .map(tourDTO -> new BookingTourResponse(bookingDTO, tourDTO))
                )
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build()) // Trả về 404 nếu không tìm thấy
                .doOnError(throwable -> log.error("Error retrieving booking from Redis: {}", throwable.getMessage()));
    }

    // Endpoint lấy danh sách bookings của customer
    @GetMapping("/redis/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BookingTourResponse> getBookingsByCustomerId(@PathVariable String customerId) {
        return redisService.getBookingsByCustomerId(customerId)
                .flatMap(bookingDTO ->
                    tourServiceClient.getTourByTicketId(bookingDTO.getTicketId())
                            .map(tourDTO -> new BookingTourResponse(bookingDTO, tourDTO))
                )
                .doOnError(error -> log.error("Error fetching bookings for customer {}: {}", customerId, error.getMessage()));
    }

    @GetMapping("/verify-booking-tour")
    public Mono<ResponseEntity<Object>> verifyBookingTour(@RequestParam("bookingId") String bookingId, @RequestParam("redirectUrl") String redirectUrl) {
        log.info("Received request to verify booking tour. bookingId: {}, redirectUrl: {}", bookingId, redirectUrl);

        Claims claims;
        try {
            claims = jwtUtils.extractAllClaims(bookingId);
            log.info("Extracted claims from JWT: {}", claims);
        } catch (Exception e) {
            log.error("Error extracting claims from JWT. bookingId: {}, error: {}", bookingId, e.getMessage());
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JWT token"));
        }

        String key = claims.get("bookingId", String.class);
        log.info("Extracted booking key from claims: {}", key);

        return redisService.getDataAsBookingDTO(key) // Phải trả về Mono<BookingDTO>
                .flatMap(bookingDTO -> {
                    log.info("BookingDTO retrieved from Redis: {}", bookingDTO);

                    bookingDTO.setStatusBooking(StatusBooking.CONFIRMED);
                    log.info("Updated BookingDTO status to CONFIRMED: {}", bookingDTO);
                    // Kiểm tra nếu customerId là null và gán là "guest"
                    String customerId = bookingDTO.getCustomerId() != null ? bookingDTO.getCustomerId().toString() : "guest";

                    return redisService.updateBookingForCustomer(customerId, bookingDTO.getBookingId(),bookingDTO, Duration.ofDays(1))
                            .flatMap(success -> {
                                if (success) {
                                    String redirectUrlWithBookingId = redirectUrl + "?bookingId=" + key;
                                    URI uri = URI.create(redirectUrlWithBookingId);
                                    log.info("Successfully saved booking. Redirecting to: {}", uri);
                                    return Mono.just(ResponseEntity.status(HttpStatus.FOUND).location(uri).build());
                                } else {
                                    log.error("Failed to save booking to Redis: {}", bookingDTO);
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                                }
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Booking data not found in Redis for key: {}", key);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                }))
                .onErrorResume(throwable -> {
                    log.error("Unexpected error occurred during booking verification. bookingId: {}, error: {}", bookingId, throwable.getMessage(), throwable);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred"));
                });
    }

    @GetMapping
    public Flux<BookingTourResponse> getBookingsByCustomerId(@RequestParam Long customerId) {
        return bookingService.getBookingsByCustomerId(customerId)
                .flatMap(bookingDTO ->
                        tourServiceClient.getTourByTicketId(bookingDTO.getTicketId())
                                .map(tourDTO -> new BookingTourResponse(bookingDTO, tourDTO))
                )
                .doOnError(error -> log.error("Error fetching bookings for customer {}: {}", customerId, error.getMessage()));
    }

}
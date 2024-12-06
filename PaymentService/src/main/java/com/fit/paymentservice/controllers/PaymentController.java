package com.fit.paymentservice.controllers;

import com.fit.paymentservice.dtos.PaymentDTO;
import com.fit.paymentservice.dtos.request.PaymentRequest;
import com.fit.paymentservice.dtos.response.RefundResponseDTO;
import com.fit.paymentservice.enums.RefundStatus;
import com.fit.paymentservice.enums.StatusBooking;
import com.fit.paymentservice.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private TourServiceClient tourServiceClient;

    @PostMapping("/process-refund")
    public Mono<ResponseEntity<RefundResponseDTO>> processRefund(@RequestParam String bookingId) {
        return refundService.processRefund(bookingId)
                .flatMap(transactionId -> bookingService.findById(bookingId)
                        .flatMap(bookingDTO -> tourServiceClient.refundSlotTourTicket(bookingDTO.getTicketId(), bookingDTO.getQuantity())
                                .map(updatedTicket -> {
                                    RefundResponseDTO response = new RefundResponseDTO();
                                    response.setTransactionId(transactionId);
                                    response.setStatus(RefundStatus.COMPLETED);
                                    response.setMessage("Refund completed and ticket slots updated.");
                                    return new ResponseEntity<>(response, HttpStatus.OK);
                                })))
                .onErrorResume(error -> {
                    log.error("Error processing refund for booking {}: {}", bookingId, error.getMessage());
                    RefundResponseDTO errorResponse = new RefundResponseDTO();
                    errorResponse.setMessage("Refund failed: " + error.getMessage());
                    return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }


    @PostMapping("/success")
    public Mono<ResponseEntity<PaymentDTO>> successPayment(@RequestBody PaymentRequest paymentRequest) {
        log.info("Received payment request: {}", paymentRequest.toString());

        return redisService.getDataAsBookingDTO(paymentRequest.getBookingId()) // Trả về Mono<BookingDTO>
                .flatMap(booking -> {
                    // Kiểm tra nếu customerId là null và gán là "guest"
                    String customerId = booking.getCustomerId() != null ? booking.getCustomerId().toString() : "guest";

                    return redisService.getBookingByBookingIdFromRedisSet(customerId, booking.getBookingId())
                            .flatMap(bookingDTO -> {
                                log.info("Received a booking: {}", bookingDTO.toString());

                                // Xóa booking trong Redis trước khi cập nhật trạng thái
                                return redisService.deleteDataFromSet("customer:" + customerId + ":bookings", bookingDTO)
                                        .then(Mono.defer(() -> {
                                            // Sau khi xóa, cập nhật trạng thái booking
                                            bookingDTO.setStatusBooking(StatusBooking.PAID);
                                            log.info("Updated booking status to PAID: {}", bookingDTO);

                                            // Lưu booking tour và thêm payment
                                            return bookingService.saveBookingTour(bookingDTO)
                                                    .then(paymentService.addPayment(paymentRequest))
                                                    .flatMap(paymentDTO -> {
                                                        // Trả về kết quả PaymentDTO
                                                        return Mono.just(ResponseEntity.ok(paymentDTO));
                                                    });
                                        }));
                            });
                })
                .onErrorResume(e -> {
                    // Log lỗi nếu có
                    log.error("Error processing payment: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
                });
    }


}
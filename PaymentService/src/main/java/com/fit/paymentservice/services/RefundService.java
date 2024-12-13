package com.fit.paymentservice.services;

import com.fit.paymentservice.enums.RefundStatus;
import com.fit.paymentservice.enums.RefundType;
import com.fit.paymentservice.enums.StatusBooking;
import com.fit.paymentservice.models.Booking;
import com.fit.paymentservice.models.Refund;
import com.fit.paymentservice.repositories.BookingRepository;
import com.fit.paymentservice.repositories.PaymentRepository;
import com.fit.paymentservice.repositories.RefundRepository;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.DetailedRefund;
import com.paypal.api.payments.RefundRequest;
import com.paypal.api.payments.Sale;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class RefundService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final BookingRepository bookingRepository;
    private final RefundRepository refundRepository;
    private final APIContext apiContext;
    private final TourServiceClient tourServiceClient;
    private final PaymentRepository paymentRepository;

    public RefundService(BookingRepository bookingRepository,
                         RefundRepository refundRepository,
                         APIContext apiContext, TourServiceClient tourServiceClient, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.refundRepository = refundRepository;
        this.apiContext = apiContext;
        this.tourServiceClient = tourServiceClient;
        this.paymentRepository = paymentRepository;
    }

    public Mono<String> processRefund(String bookingId) {
        return bookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Booking not found for bookingId: " + bookingId)))
                .flatMap(booking -> paymentRepository.findPaymentByBookingId(booking.getBookingId())
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Payment not found for booking ID: " + bookingId)))
                        .flatMap(payment -> calculateRefundAmount(booking)
                                .flatMap(amount -> {
                                    if (amount <= 0) {
                                        // Nếu số tiền hoàn là 0, trả về lỗi và không tiếp tục thực hiện
                                        return updateBookingStatus(booking, StatusBooking.REFUNDED)
                                                .then(Mono.error(new IllegalArgumentException("Refund amount cannot be zero. No refund processed.")));
                                    }
                                    // Nếu số tiền > 0, thực hiện tiếp quy trình hoàn tiền
                                    return saveRefundRecord(booking, payment.getPaymentId(), amount)
                                            .flatMap(refund -> refundThroughPayPal(payment.getTransactionId(), refund.getAmount())
                                                    .flatMap(transactionId -> updateRefundStatus(refund, RefundStatus.COMPLETED, transactionId)
                                                            .then(updateBookingStatus(booking, StatusBooking.REFUNDED))
                                                            .thenReturn(transactionId) // Trả về transactionId sau khi hoàn tất
                                                    ));
                                })
                        ));
    }



    private Mono<Double> calculateRefundAmount(Booking booking) {
        log.info("Calculate Refund!");
        // Lấy ngày khởi hành từ TicketTour
        return tourServiceClient.getTourTicketById(booking.getTicketId())
                .flatMap(tourTicketDTO -> {
                    LocalDate departureDate = tourTicketDTO.getDepartureDate();
                    long daysToDeparture = ChronoUnit.DAYS.between(LocalDate.now(), departureDate);

                    boolean isHoliday = checkIfHoliday(departureDate); // Kiểm tra ngày lễ/tết
                    double refundPercentage;

                    if (isHoliday) {
                        if (daysToDeparture > 10) refundPercentage = 0.5;
                        else if (daysToDeparture >= 3) refundPercentage = 0.25;
                        else refundPercentage = 0.0;
                    } else {
                        if (daysToDeparture > 10) refundPercentage = 0.75;
                        else if (daysToDeparture >= 5) refundPercentage = 0.5;
                        else if (daysToDeparture >= 2) refundPercentage = 0.25;
                        else refundPercentage = 0.0;
                    }
                    log.info("Amount: "+booking.getTotalAmount());
                    log.info("RefundPercentage: "+ refundPercentage);
                    double refundAmount = booking.getTotalAmount() * refundPercentage;
                    return Mono.just(refundAmount);
                });
    }

    private boolean checkIfHoliday(LocalDate date) {
        // Lấy ngày tháng năm hiện tại
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();

        // Tập hợp các ngày lễ cố định hàng năm
        Set<LocalDate> fixedHolidays = Set.of(
                LocalDate.of(year, 1, 1),  // Tết Dương Lịch
                LocalDate.of(year, 4, 30), // Ngày Giải Phóng Miền Nam
                LocalDate.of(year, 5, 1),  // Quốc Tế Lao Động
                LocalDate.of(year, 9, 2),  // Quốc Khánh Việt Nam
                LocalDate.of(year, 12, 25), // Noel
                LocalDate.of(year, 2, 14)  // Valentine
        );

        // Kiểm tra nếu ngày thuộc vào các ngày lễ cố định
        if (fixedHolidays.contains(date)) {
            return true;
        }

        // Tính toán Tết Nguyên Đán dựa trên lịch Âm Dương (giả sử năm nay Tết là ngày 10/02)
        LocalDate tetHoliday = getTetHoliday(year);

        // Kiểm tra các ngày liên quan đến Tết Nguyên Đán (mùng 1 đến mùng 10)
        if (!date.isBefore(tetHoliday) && !date.isAfter(tetHoliday.plusDays(9))) {
            return true;
        }

        return false; // Không phải ngày lễ
    }

    private LocalDate getTetHoliday(int year) {
        // Danh sách ngày Tết Nguyên Đán cố định cho từng năm
        Map<Integer, LocalDate> tetDates = Map.of(
                2024, LocalDate.of(2024, 2, 10), // Năm 2024: Mùng 1 Tết vào 10/02/2024
                2025, LocalDate.of(2025, 1, 29), // Năm 2025: Mùng 1 Tết vào 29/01/2025
                2026, LocalDate.of(2026, 2, 17)  // Năm 2026: Mùng 1 Tết vào 17/02/2026
        );

        // Trả về ngày Tết cho năm hiện tại, mặc định 10/02/2024 nếu không tìm thấy
        return tetDates.getOrDefault(year, LocalDate.of(year, 2, 10));
    }


    private Mono<Refund> saveRefundRecord(Booking booking, String paymentId, double amount) {
        log.info("SAve refund");
        Refund refund = new Refund();
        refund.setReId(UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên cho Refund
        refund.setPaymentId(paymentId); // Gán paymentId

        refund.setRefundDate(LocalDate.now()); // Ngày hoàn tiền
        refund.setAmount(amount); // Số tiền hoàn tiền
        refund.setStatus(RefundStatus.PENDING); // Trạng thái hoàn tiền ban đầu là PENDING
        refund.setTypeRefund(amount == booking.getTotalAmount() ? RefundType.FULL : RefundType.PARTIAL); // Loại hoàn tiền (FULL hoặc PARTIAL)

        // Gọi phương thức saveRefund để thêm mới Refund vào cơ sở dữ liệu
        return refundRepository.saveRefund(refund.getReId(), refund.getPaymentId(), refund.getTransactionId(),
                refund.getRefundDate(), refund.getAmount(), refund.getStatus().toString(),
                refund.getTypeRefund().toString());
    }

    private Mono<Void> updateRefundStatus(Refund refund, RefundStatus status, String transactionId) {
        // Log the current state of the refund
        log.info("Updating refund status for reId: " + refund.getReId());

        refund.setStatus(status);
        refund.setTransactionId(transactionId); // Lưu transaction ID của PayPal
        return refundRepository.save(refund)
                .doOnError(error -> {
                    // Log error if saving fails
                    log.error("Failed to save refund with reId: " + refund.getReId());
                })
                .then();
    }


    private Mono<Void> updateBookingStatus(Booking booking, StatusBooking status) {
        booking.setStatusBooking(status);
        return bookingRepository.save(booking).then();
    }

    private APIContext createApiContext() {
        return new APIContext(clientId, clientSecret, mode);
    }


    private Mono<String> refundThroughPayPal(String transactionId, double amount) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Transaction ID: " + transactionId);

                // Tạo APIContext mới
                APIContext newApiContext = createApiContext();

                Sale sale = Sale.get(newApiContext, transactionId);

                // Tạo PayPal-Request-Id duy nhất cho mỗi yêu cầu hoàn tiền
                String uniqueRequestId = UUID.randomUUID().toString();
                log.info("Generated unique PayPal-Request-Id: " + uniqueRequestId);

                // Tạo refund request
                RefundRequest refundRequest = new RefundRequest();
                Amount refundAmount = new Amount("USD", String.format("%.2f", amount)); // Giả sử đơn vị là USD
                refundRequest.setAmount(refundAmount);

                // Thêm header PayPal-Request-Id vào newApiContext
                newApiContext.addHTTPHeader("PayPal-Request-Id", uniqueRequestId);
                log.info("Headers in newApiContext: " + newApiContext.getHTTPHeaders());

                // Thực hiện hoàn tiền
                DetailedRefund detailedRefund = sale.refund(newApiContext, refundRequest);
                return detailedRefund.getId(); // Trả về transaction ID
            } catch (PayPalRESTException e) {
                throw new RuntimeException("Refund failed: " + e.getMessage());
            }
        });
    }

}


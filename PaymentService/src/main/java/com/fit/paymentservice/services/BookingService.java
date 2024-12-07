package com.fit.paymentservice.services;

import com.fit.paymentservice.utils.Constant;
import com.fit.paymentservice.dtos.BookingDTO;
import com.fit.paymentservice.dtos.request.BookingRequest;
import com.fit.paymentservice.dtos.response.BookingResponse;
import com.fit.paymentservice.events.EventConsumer;
import com.fit.paymentservice.events.EventProducer;
import com.fit.paymentservice.repositories.BookingRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EventProducer eventProducer;

    @Qualifier("gson")
    @Autowired
    private Gson gson;
    @Autowired
    @Lazy
    private EventConsumer eventConsumer;

    public Mono<BookingResponse> createBookingTour(BookingRequest bookingRequest) {
//        Kiem tra thong tin truoc khi gui
        if (bookingRequest.getTourId() == null) {
            return Mono.error(new IllegalArgumentException("Tour ID is required"));
        }

        if (bookingRequest.getQuantity() <= 0) {
            return Mono.error(new IllegalArgumentException("Quantity is required"));
        }

        // Sinh UUID cho transactionId
        UUID bookingId = UUID.randomUUID();
        bookingRequest.setBookingId(bookingId.toString());
        log.info("bookingRequest: {}", bookingRequest);
        // Gửi message đến Kafka và chờ phản hồi từ Kafka
        return eventProducer.send(Constant.REQUEST_CHECK_AVAILABLE_SLOT_TOPIC, String.valueOf(bookingRequest.getBookingId()), gson.toJson(bookingRequest))
                .then(eventConsumer.waitForBookingResponse(bookingRequest.getBookingId()))
                .switchIfEmpty(Mono.error(new NoSuchElementException("Not found response for customerId: " + bookingRequest.getCustomerId())))
                .onErrorResume(throwable -> {
                    log.error("Error occurred: {}", throwable.getMessage());
                    return Mono.just(new BookingResponse()); // Trả măc dinh ve doi tuong rong
                });
    }

    public Mono<BookingDTO> saveBookingTour(BookingDTO bookingDTO) {
        if (bookingDTO.getBookingId() == null) {
            bookingDTO.setBookingId(UUID.randomUUID().toString());
        }

        return Mono.just(bookingDTO)
                .map(BookingDTO::convertToEntity)
                .flatMap(booking -> {
                    log.info("booking: {}", booking.toString());
                    // Sử dụng flatMap để lưu và nhận kết quả từ repository
                    return bookingRepository.saveBooking(
                            booking.getBookingId(),
                            booking.getTourId(),  // truyền tourId
                            booking.getTicketId(),
                            booking.getBookingDate(),
                            String.valueOf(booking.getStatusBooking()),
                            booking.getTotalAmount(),
                            booking.getQuantity(),
                            booking.getAdults(),
                            booking.getChildren(),
                            booking.getToddlers(),
                            booking.getInfants(),
                            booking.getCustomerId(),
                            booking.getEmail(),
                            booking.getUserName(),
                            booking.getPhoneNumber(),
                            booking.getCity(),
                            booking.getDistrict(),
                            booking.getWard(),
                            booking.getAddress()
                    );
                })
                .map(BookingDTO::convertToDto)
                .doOnError(throwable -> log.error("Error saving booking: {}", throwable.getMessage(), throwable));
    }

    public Mono<BookingDTO> findById(String bookingId){
        return bookingRepository.findById(bookingId)
                .map(BookingDTO::convertToDto);
    }

    public Flux<BookingDTO> getBookingsByCustomerId(Long customerId){
        return bookingRepository.findBookingByCustomerId(customerId)
                .map(BookingDTO::convertToDto);
    }


    public BookingDTO mapBookingResponseToDTO(BookingRequest bookingRequest, BookingResponse bookingResponse) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(bookingRequest.getBookingId());
        bookingDTO.setTicketId(bookingRequest.getTicketId());
        bookingDTO.setTourId(bookingRequest.getTourId());
        bookingDTO.setStatusBooking(bookingResponse.getStatusBooking());
        bookingDTO.setBookingDate(bookingResponse.getBookingDate());
        bookingDTO.setTotalAmount(bookingResponse.getTotalAmount());
        bookingDTO.setQuantity(bookingResponse.getQuantity());
        bookingDTO.setCustomerId(bookingRequest.getCustomerId());
        bookingDTO.setUserName(bookingRequest.getUserName());
        bookingDTO.setEmail(bookingRequest.getEmail());
        bookingDTO.setPhoneNumber(bookingRequest.getPhoneNumber());
        bookingDTO.setAddress(bookingRequest.getAddress());
        bookingDTO.setCity(bookingRequest.getCity());
        bookingDTO.setDistrict(bookingRequest.getDistrict());
        bookingDTO.setWard(bookingRequest.getWard());
        return bookingDTO;
    }

    public Mono<ResponseEntity<BookingDTO>> sendBookingNotification(BookingDTO savedBooking) {
        return eventProducer.send(Constant.NOTIFICATION_BOOKING_TOUR_TOPIC, String.valueOf(savedBooking.getBookingId()), gson.toJson(savedBooking))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(savedBooking));
    }

}

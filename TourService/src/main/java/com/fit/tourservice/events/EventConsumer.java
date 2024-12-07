package com.fit.tourservice.events;

import com.fit.tourservice.dtos.TourDTO;
import com.fit.tourservice.dtos.request.BookingRequest;
import com.fit.tourservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.tourservice.dtos.response.BookingResponse;
import com.fit.tourservice.enums.*;
import com.fit.tourservice.services.RedisService;
import com.fit.tourservice.services.TourService;
import com.fit.tourservice.services.TourTicketService;
import com.fit.tourservice.utils.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventConsumer {
    private final KafkaReceiver<String, String> kafkaReceiver;
    private Sinks.Many<TourDTO> sinkForPreferences;
    private Sinks.Many<TourDTO> sinkForInteractions;

    @Autowired
    private Gson gson;
    @Autowired
    private TourService tourService;
    private boolean isSinkForPreferencesTerminated;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private TourTicketService tourTicketService;
    @Autowired
    private RedisService redisService;

    public EventConsumer(ReceiverOptions<String, String> options) {
        log.info("RecomendationConsumer started");
        // Tạo KafkaReceiver với các topic
        this.kafkaReceiver = KafkaReceiver.create(
                options.subscription(Set.of(
                        Constant.RECOMMEND_PREFERENCES_TOPIC,
                        Constant.RECOMMEND_INTERACTED_TOPIC,
                        Constant.REQUEST_CHECK_AVAILABLE_SLOT_TOPIC
                ))
        );

        // Khởi tạo các sink
        this.sinkForPreferences = Sinks.many().multicast().onBackpressureBuffer();
        this.sinkForInteractions = Sinks.many().multicast().onBackpressureBuffer();

        this.kafkaReceiver.receive()
                .flatMap(this::processRecord) // Xử lý từng message từ Kafka
                .subscribe(
                        result -> log.info("Successfully processed recommendation event"),
                        error -> log.error("Error processing recommendation event", error)
                );
    }

    private Mono<Void> processRecord(ReceiverRecord<String, String> receiverRecord) {
        String topic = receiverRecord.topic();
        log.info("receiverRecord: {}", receiverRecord);
        if (Constant.RECOMMEND_PREFERENCES_TOPIC.equals(topic)) {
            return getLstTourByRecommendationPreferenceReceived(receiverRecord);
        } else if (Constant.RECOMMEND_INTERACTED_TOPIC.equals(topic)) {
            return getLstTourByRecommendationInteractionReceived(receiverRecord);
        } else if (Constant.REQUEST_CHECK_AVAILABLE_SLOT_TOPIC.equals(topic)) {
            return processBookingRequest(receiverRecord);
        } else {
            log.warn("Unknown topic: {}", topic);
            return Mono.empty();
        }
    }

    private Mono<Void> processBookingRequest(ReceiverRecord<String, String> receiverRecord) {
        log.info("Received booking request: {}", receiverRecord.value());
        BookingRequest bookingRequest = gson.fromJson(receiverRecord.value(), BookingRequest.class);

        String lockKey = "lock:ticket" + bookingRequest.getTicketId();
        log.info("lockKey: {}", lockKey);

        return acquireLockWithRetry(lockKey)
                .flatMap(locked -> {
                    if (!locked) {
                        log.warn("Could not acquire lock for ticketId: {}", bookingRequest.getTicketId());
                        BookingResponse errorResponse = new BookingResponse();
                        errorResponse.setBookingId(bookingRequest.getBookingId());
                        errorResponse.setAvailable(false);

                        return eventProducer.send(Constant.RESPONSE_BOOKING_TOPIC,
                                        String.valueOf(bookingRequest.getCustomerId()),
                                        gson.toJson(errorResponse))
                                .doOnSuccess(result -> log.info("Sent error response to booking-response topic: {}", result))
                                .then();
                    }

                    return tourTicketService.checkAvailableSlot(bookingRequest.getTicketId(), bookingRequest.getQuantity())
                            .flatMap(isAvailable -> {
                                BookingResponse bookingResponseDTO = new BookingResponse();
                                bookingResponseDTO.setBookingId(bookingRequest.getBookingId());
                                bookingResponseDTO.setQuantity(bookingRequest.getQuantity());
                                bookingResponseDTO.setAvailable(isAvailable);

                                if (isAvailable) {
                                    bookingResponseDTO.setBookingDate(LocalDate.now());
                                    bookingResponseDTO.setStatusBooking(StatusBooking.PENDING_CONFIRMATION);
                                    log.info("bookingResponseDTO demo: {}", bookingResponseDTO);

                                    // Kiểm tra và cập nhật slot vé có sẵn
                                    return tourTicketService.updateAvailableSlot(bookingRequest.getTicketId(), bookingRequest.getQuantity())
                                            .flatMap(updated -> {
                                                double totalAmount = bookingRequest.getTotalAmount();
                                                bookingResponseDTO.setTotalAmount(totalAmount);

                                                log.info("Total amount from bookingRequest: {}", totalAmount);
                                                log.info("BookingResponseDTO: {}", bookingResponseDTO);

                                                // Gửi phản hồi vào topic
                                                return eventProducer.send(Constant.RESPONSE_BOOKING_TOPIC,
                                                                String.valueOf(bookingRequest.getBookingId()),
                                                                gson.toJson(bookingResponseDTO))
                                                        .doOnSuccess(result -> log.info("Sent response to booking-response topic: {}", result))
                                                        .then();
                                            });
                                } else {
                                    return eventProducer.send(Constant.RESPONSE_BOOKING_TOPIC,
                                                    String.valueOf(bookingRequest.getCustomerId()),
                                                    gson.toJson(bookingResponseDTO))
                                            .doOnSuccess(result -> log.info("Sent response to booking-response with error topic: {}", result))
                                            .then();
                                }
                            })
                            .doFinally(signalType -> redisService.releaseLock(lockKey).subscribe()); // Giải phóng khóa
                })
                .doOnTerminate(() -> {
                    // Acknowledge record đã được xử lý
                    receiverRecord.receiverOffset().acknowledge();
                });
    }

    //Cơ chế Retry lockey
    private Mono<Boolean> acquireLockWithRetry(String lockKey) {
        int maxRetries = 3;
        return Mono.defer(() -> redisService.acquireLock(lockKey))
                .retryWhen(Retry.fixedDelay(maxRetries, Duration.ofSeconds(1)))
                .doOnNext(locked -> {
                    if (!locked) {
                        log.warn("Failed to acquire lock for {} after {} attempts", lockKey, maxRetries);
                    }
                });
    }


    private Mono<Void> getLstTourByRecommendationInteractionReceived(ReceiverRecord<String, String> receiverRecord) {
        try {
            // Chuyển đổi dữ liệu từ Kafka message thành Map<String, Object>
            Map<String, Object> reqMap = gson.fromJson(receiverRecord.value(), new TypeToken<Map<String, Object>>() {
            }.getType());

            // Lấy customerId từ message
            if (reqMap.containsKey("customerId") && reqMap.get("customerId") instanceof Number) {
                Long customerId = ((Number) reqMap.get("customerId")).longValue();
                log.info("Received customerId: {}", customerId);

                // Lấy danh sách tourIds từ message
                if (reqMap.containsKey("recommendedTourIds") && reqMap.get("recommendedTourIds") instanceof List) {
                    List<Long> tourIds = ((List<Double>) reqMap.get("recommendedTourIds"))
                            .stream()
                            .map(Double::longValue)  // Chuyển đổi từng phần tử từ Double thành Long
                            .collect(Collectors.toList());
                    log.info("Received tourIds: {}", tourIds);

                    if (tourIds != null && !tourIds.isEmpty()) {
                        log.info("Received valid tourIds: {}", tourIds);
                        // Lọc các tour theo tourId và chuyển đổi thành Flux<TourDTO>
                            return tourService.findToursByIds(tourIds)
                                // Xử lý từng TourDTO
                                .doOnNext(tourDTO -> {
                                    log.info("Found tour: {}", tourDTO);
                                    sinkForInteractions.tryEmitNext(tourDTO);  // Gửi tourDTO qua sink
                                })
                                // Xử lý hoàn tất
                                .doOnComplete(() -> {
                                    log.info("All tours emitted, completing sink for customerId: {}", customerId);
                                    sinkForInteractions.tryEmitComplete();  // Hoàn thành sink

                                    // Tái khởi tạo sink để sẵn sàng cho yêu cầu tiếp theo
                                    this.resetSinkForInteractions();
                                })
                                // Xử lý lỗi nếu có
                                .doOnError(error -> log.error("Error retrieving tours for customerId: {}", customerId, error))
                                // Chuyển đổi Flux thành Mono<Void> sau khi hoàn tất
                                .then();  // `.then()` trả về Mono<Void> khi hoàn tất xử lý
                    } else {
                        log.warn("No tour IDs found in the message for customerId: {}", customerId);
                        return Mono.empty();  // Trả về Mono.empty() nếu không có tourIds
                    }
                } else {
                    log.warn("Invalid or missing 'recommendedTourIds' in the message for customerId: {}", customerId);
                    return Mono.empty();
                }
            } else {
                log.error("Invalid or missing 'customerId' in the message");
                return Mono.empty();
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON message: {}", receiverRecord.value(), e);
            return Mono.empty();
        }
    }

    // Hàm để reset lại sink sau mỗi yêu cầu
    private void resetSinkForInteractions() {
        // Tái khởi tạo sink để chuẩn bị cho yêu cầu tiếp theo
        this.sinkForInteractions = Sinks.many().multicast().onBackpressureBuffer();
        log.info("Sink for interactions has been reset for the next request");
    }


    private Mono<Void> getLstTourByRecommendationPreferenceReceived(ReceiverRecord<String, String> receiverRecord) {
        try {
            Map<String, Object> reqMap = gson.fromJson(receiverRecord.value(), new TypeToken<Map<String, Object>>() {
            }.getType());

            // Kiểm tra customerId từ message
            if (reqMap.containsKey("customerId") && reqMap.get("customerId") instanceof Number) {
                Long customerId = ((Number) reqMap.get("customerId")).longValue();
                log.info("Processing tours for CustomerId: {}", customerId);

                // Lấy commonPreferences từ reqMap để tạo TourFilterCriteriaRequest
                if (reqMap.containsKey("commonPreferences") && reqMap.get("commonPreferences") instanceof Map) {
                    Map<String, Object> commonPreferences = (Map<String, Object>) reqMap.get("commonPreferences");

                    // Tạo TourFilterCriteriaRequest từ commonPreferences
                    TourFilterCriteriaRequest criteriaRequest = getCriteriaRequest(commonPreferences);

                    // Kiểm tra nếu `criteriaRequest` hợp lệ
                    if (criteriaRequest == null) {
                        log.error("CriteriaRequest is null for CustomerId: {}", customerId);
                        return Mono.empty();
                    }

                    // Tìm kiếm tour dựa trên tiêu chí và xử lý kết quả cho từng customerId
                    return tourService.findToursByCriteria(criteriaRequest, 1, 10) // Chọn page và size tùy yêu cầu
                            .flatMapMany(page -> Flux.fromIterable(page.getContent()))
                            .doOnNext(tour -> {
                                log.info("Emitting tour for CustomerId {}: {}", customerId, tour);
                                // Gửi từng tour tới subscribers thông qua sink, có thể tạo sink riêng cho mỗi customerId
                                sinkForPreferences.tryEmitNext(tour);
                            })
                            .doOnComplete(() -> {
                                log.info("All tours emitted for CustomerId: {}, completing sink", customerId);
                                sinkForPreferences.tryEmitComplete(); // Hoàn thành sink cho customerId hiện tại

                                // Tái khởi tạo sink để sẵn sàng cho yêu cầu tiếp theo từ Kafka
                                resetSinkForPreferences();
                            })
                            .then()
                            .onErrorResume(error -> {
                                log.error("Error during tour filtering for CustomerId: {}: ", customerId, error);
                                // Tái khởi tạo sink nếu gặp lỗi để tránh ảnh hưởng tới các yêu cầu khác
                                resetSinkForPreferences();
                                return Mono.empty();
                            });
                } else {
                    log.error("commonPreferences not found or invalid format in the message");
                    return Mono.empty();
                }
            } else {
                log.error("Invalid customerId format in the message");
                return Mono.empty();
            }
        } catch (JsonSyntaxException e) {
            log.error("Error parsing JSON message: {}", receiverRecord.value(), e);
            return Mono.empty();
        }

    }

    private TourFilterCriteriaRequest getCriteriaRequest(Map<String, Object> criteriaMap) {
        log.info("Received recommendation event");

//        // Chuyển message từ Kafka thành Map<String, Object>
//        Map<String, Object> criteriaMap = gson.fromJson(receiverRecord.value(), new TypeToken<Map<String, Object>>() {
//        }.getType());

        // Tạo TourFilterCriteriaRequest từ các tiêu chí
        TourFilterCriteriaRequest criteriaRequest = new TourFilterCriteriaRequest();
        if (criteriaMap.containsKey("maxPrice")) {
            Object maxPriceObj = criteriaMap.get("maxPrice");
            log.info("maxPrice type: {}", maxPriceObj.getClass().getName());
            Double maxPrice = (maxPriceObj instanceof Number) ? ((Number) maxPriceObj).doubleValue() : null;
            criteriaRequest.setMaxCost(maxPrice);
        }

        if (criteriaMap.containsKey("maxDuration")) {
            Object maxDurationObj = criteriaMap.get("maxDuration");
            log.info("maxDuration type: {}", maxDurationObj.getClass().getName());
            Integer maxDuration = (maxDurationObj instanceof Number) ? ((Number) maxDurationObj).intValue() : null;
            criteriaRequest.setMaxDuration(maxDuration);
            log.info("Converted maxDuration: {}", maxDuration);
        }

        criteriaRequest.setStartDate(LocalDate.now());
        try {
            criteriaRequest.setTypeTour(TypeTour.valueOf((String) criteriaMap.get("type")));
            criteriaRequest.setRegion(Region.valueOf((String) criteriaMap.get("region")));
            criteriaRequest.setAccommodationQuality(AccommodationQuality.valueOf((String) criteriaMap.get("accommodation")));
            criteriaRequest.setTransportationMode(TransportationMode.valueOf((String) criteriaMap.get("transportationMode")));
            log.info("criteriaRequest: {}", criteriaRequest);
        } catch (IllegalArgumentException e) {
            log.error("Error in enum conversion: ", e);
        }
        return criteriaRequest;
    }

    public Flux<TourDTO> getPreferenceTourStream() {
        return sinkForPreferences.asFlux();
    }

    public Flux<TourDTO> getInteractionTourStream() {
        return sinkForInteractions.asFlux();
    }

    // Hàm để reset lại sink sau mỗi yêu cầu
    private void resetSinkForPreferences() {
        // Tái khởi tạo sink để chuẩn bị cho yêu cầu tiếp theo
        this.sinkForPreferences = Sinks.many().multicast().onBackpressureBuffer();
        log.info("Sink for preferences has been reset for the next request");
    }

}


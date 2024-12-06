package com.fit.paymentservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.paymentservice.dtos.BookingDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisService(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate, @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.objectMapper = objectMapper;
    }

    // Lưu dữ liệu vào Redis với TTL
    public Mono<Boolean> saveDataWithTTL(String key, Object value, Duration ttl) {
        return reactiveRedisTemplate.opsForValue().set(key, value, ttl)
                .doOnSuccess(success -> log.info("Saved key: {} with TTL: {}", key, ttl))
                .doOnError(error -> log.error("Failed to save key: {}", key, error));
    }

    public Mono<Boolean> deleteDataFromSet(String key, Object value) {
        return reactiveRedisTemplate.opsForSet()
                .remove(key, value)  // This returns a Mono<Long>
                .map(removed -> removed > 0)  // Convert the result to Mono<Boolean>
                .doOnSuccess(removed -> {
                    if (removed) {
                        log.info("Successfully removed value: {} from set with key: {}", value, key);
                    } else {
                        log.warn("Value: {} not found in set with key: {}", value, key);
                    }
                })
                .doOnError(error -> log.error("Failed to remove value: {} from set with key: {}", value, key, error));
    }

    public Mono<BookingDTO> getBookingByBookingIdFromRedisSet(String customerId, String bookingId) {
        String key = "customer:" + customerId + ":bookings";

        // Lấy các phần tử trong Redis Set, mỗi phần tử là LinkedHashMap
        return reactiveRedisTemplate.opsForSet().members(key)
                .flatMap(booking -> {
                    // Kiểm tra nếu booking là LinkedHashMap
                    if (booking instanceof LinkedHashMap) {
                        // Chuyển đổi phần tử từ LinkedHashMap sang BookingDTO
                        BookingDTO bookingDTO = objectMapper.convertValue(booking, BookingDTO.class);
                        // Kiểm tra nếu bookingId trùng khớp
                        if (bookingDTO.getBookingId() != null && bookingDTO.getBookingId().equals(bookingId)) {
                            return Mono.just(bookingDTO); // Trả về bookingDTO nếu tìm thấy
                        }
                    }
                    return Mono.empty(); // Không tìm thấy bookingId trong phần tử này
                })
                .next()  // Lấy phần tử đầu tiên tìm thấy, hoặc Mono.empty() nếu không có phần tử nào
                .doOnSuccess(bookingDTO -> {
                    if (bookingDTO != null) {
                        log.info("Found booking for customer {} with bookingId {}: {}", customerId, bookingId, bookingDTO);
                    }
                })
                .doOnError(error -> log.error("Failed to fetch booking for customer {} with bookingId {}: {}", customerId, bookingId, error));
    }

    // Lấy dữ liệu từ Redis
    public Mono<Object> getData(String key) {
        return reactiveRedisTemplate.opsForValue().get(key);
    }

    // Lấy dữ liệu và chuyển đổi thành BookingDTO
    public Mono<BookingDTO> getDataAsBookingDTO(String key) {
        return getData(key)
                .flatMap(data -> {
                    if (data instanceof LinkedHashMap<?, ?>) {
                        // Chuyển đổi từ LinkedHashMap sang BookingDTO
                        BookingDTO bookingDTO = objectMapper.convertValue(data, BookingDTO.class);
                        return Mono.just(bookingDTO);
                    } else {
                        return Mono.error(new ClassCastException("Data is not of type LinkedHashMap"));
                    }
                });
    }

    public Mono<Boolean> addBookingToCustomer(String customerId, Object value, Duration ttl) {
        String key = "customer:" + customerId + ":bookings";

        // Thêm bookingId vào Redis Set
        return reactiveRedisTemplate.opsForSet().add(key, value)
                .flatMap(count -> {
                    if (count > 0) {
                        // Nếu bookingId được thêm thành công, thiết lập TTL cho key Redis
                        return reactiveRedisTemplate.expire(key, ttl)
                                .map(expireSuccess -> expireSuccess);
                    }
                    return Mono.just(false);  // Nếu không thêm được phần tử vào set
                })
                .map(expireSuccess -> expireSuccess)  // Trả về true nếu TTL được thiết lập thành công
                .doOnSuccess(success -> log.info("Added booking {} to customer {} with TTL: {}", value, customerId, ttl))
                .doOnError(error -> log.error("Failed to add booking to customer: {}", error));
    }

    public Mono<Boolean> updateBookingForCustomer(String customerId, String bookingId, Object newValue, Duration ttl) {
        String key = "customer:" + customerId + ":bookings";

        // Lấy các phần tử trong Redis Set, mỗi phần tử là LinkedHashMap
        return reactiveRedisTemplate.opsForSet().members(key)
                .flatMap(booking -> {
                    // Kiểm tra nếu bookingId trong booking trùng với bookingId cần cập nhật
                    if (booking instanceof LinkedHashMap) {
                        BookingDTO bookingDTO = objectMapper.convertValue(booking, BookingDTO.class);
                        String currentBookingId = bookingDTO.getBookingId();
                        if (currentBookingId != null && currentBookingId.equals(bookingId)) {
                            // Xóa phần tử cũ (LinkedHashMap) khỏi Set
                            return reactiveRedisTemplate.opsForSet().remove(key, booking)
                                    .flatMap(removedCount -> {
                                        if (removedCount > 0) {
                                            // Thêm phần tử mới vào Set
                                            return reactiveRedisTemplate.opsForSet().add(key, newValue)
                                                    .flatMap(count -> {
                                                        if (count > 0) {
                                                            // Thiết lập TTL cho key Redis
                                                            return reactiveRedisTemplate.expire(key, ttl)
                                                                    .thenReturn(true); // Thành công
                                                        }
                                                        return Mono.just(false);  // Không thể thêm phần tử mới
                                                    });
                                        }
                                        return Mono.just(false);  // Không thể xóa phần tử cũ
                                    });
                        }
                    }
                    return Mono.just(false); // Không tìm thấy bookingId trong Set
                })
                .collectList()  // Chuyển đổi Flux thành List (chúng ta chỉ cần một kết quả cuối cùng)
                .map(list -> list.stream().anyMatch(Boolean::booleanValue))  // Kiểm tra xem có bất kỳ phần tử nào thành công không
                .defaultIfEmpty(false)  // Nếu Flux trống (không có phần tử nào để cập nhật), trả về false
                .doOnTerminate(() -> log.info("Update process completed for customer {}", customerId)) // Log khi kết thúc
                .doOnError(error -> log.error("Failed to update booking for customer {}: {}", customerId, error));
    }

    // Lấy danh sách bookings của customer từ Redis
    public Flux<BookingDTO> getBookingsByCustomerId(String customerId) {
        String key = "customer:" + customerId + ":bookings";  // Key của Redis Set chứa các bookingId

        return reactiveRedisTemplate.opsForSet().members(key)
                .doOnNext(booking -> {
                    if (booking != null) {
                        log.info("Fetched booking from Redis for customer {}: {} (Type: {})",
                                customerId, booking, booking.getClass().getName());
                    } else {
                        log.warn("No booking found for customer {} in Redis.", customerId);
                    }
                })
                .flatMap(booking -> {
                    if (booking == null) {
                        log.warn("Booking is null for customer {} in Redis.", customerId);
                        return Mono.empty();  // Nếu booking là null, trả về Mono.empty
                    }

                    // Chuyển đổi từ LinkedHashMap sang BookingDTO
                    if (booking instanceof LinkedHashMap) {
                        BookingDTO bookingDTO = objectMapper.convertValue(booking, BookingDTO.class);
                        return Mono.just(bookingDTO);
                    } else {
                        return Mono.error(new IllegalArgumentException("Unexpected booking type: " + booking.getClass().getName()));
                    }
                })
                .doOnTerminate(() -> log.info("Returning bookings for customer {}", customerId));
    }

    // Lưu thông tin Booking và thêm bookingId vào Redis Set của customer
    public Mono<Boolean> saveBookingTourFromRedis(BookingDTO bookingDTO) {
        String bookingKey = bookingDTO.getBookingId().toString();

        // Lưu thông tin BookingDTO vào Redis với TTL
        Mono<Boolean> saveBookingMono = saveDataWithTTL(bookingKey, bookingDTO, Duration.ofDays(1));

        // Lưu bookingId vào Redis Set của customerId
        String customerId = (bookingDTO.getCustomerId() != null) ? bookingDTO.getCustomerId().toString() : "guest";
        Mono<Boolean> addBookingToCustomerMono = addBookingToCustomer(customerId, bookingDTO, Duration.ofDays(1));

        // Kết hợp cả hai Mono để thực hiện đồng thời
        return Mono.zip(saveBookingMono, addBookingToCustomerMono)
                .map(tuple -> tuple.getT1() && tuple.getT2())  // Kiểm tra cả hai thao tác đã thành công
                .doOnError(throwable -> log.error("Error saving booking and adding to customer: {}", throwable.getMessage(), throwable));
    }
}



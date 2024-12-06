package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.tourservice.dtos.TourDTO;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TypeTour;
import com.fit.tourservice.events.EventConsumer;
import com.fit.tourservice.services.TourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tours")
@Slf4j
public class TourController {
    @Autowired
    private TourService tourService;
    @Autowired
    private EventConsumer eventConsumer;


    @PostMapping("/add")
    public Mono<ResponseEntity<TourDTO>> addTour(@RequestBody TourDTO tourDTO) {
        return tourService.addTour(tourDTO)
                .map(savedTour -> ResponseEntity.status(HttpStatus.CREATED).body(savedTour))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{tourId}")
    public Mono<ResponseEntity<TourDTO>> updateTour(@PathVariable Long tourId, @RequestBody TourDTO tourDTO) {
        return tourService.updateTour(tourDTO, tourId)
                .map(updatedTour -> ResponseEntity.ok(updatedTour))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{tourId}")
    public Mono<ResponseEntity<String>> deleteTour(@PathVariable Long tourId) {
        return tourService.deleteTour(tourId)
                .then(Mono.just(ResponseEntity.ok("Tour with ID " + tourId + " has been deleted."))) // Trả về thông báo xác nhận
                .onErrorResume(error -> {
                    log.error("Error while deleting tour {}: {}", tourId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    @GetMapping
    public Mono<ResponseEntity<Flux<TourDTO>>> getAllTours(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(tourService.getAllTours(page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching tours: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/getById")
    public Mono<ResponseEntity<TourDTO>> getTourByTicketId( @RequestParam Long ticketId) {
        return tourService.getTourById(ticketId)
                .map(ResponseEntity::ok) // Nếu thành công, trả về ResponseEntity với HTTP 200
                .onErrorResume(NoSuchElementException.class, e -> {
                    log.warn("Tour not found: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
                })
                .onErrorResume(e -> {
                    log.error("Unexpected error fetching tour: {}", e.getMessage(), e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    @GetMapping("/getToursByIds")
    public Mono<List<TourDTO>> getToursByIds(@RequestParam String tourIds, // nhận tham số tourIds dưới dạng String
                                             @RequestParam int page,
                                             @RequestParam int size) {
        List<Long> tourIdList = Arrays.stream(tourIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList()); // chuyển chuỗi thành List<Long>

        return tourService.findToursByIds(tourIdList)
                .skip((page-1) * size)
                .take(size)
                .collectList();
    }

    // API lấy danh sách tour theo region
    @GetMapping("/region")
    public Mono<ResponseEntity<Map<String, Object>>> getTourByRegion(
            @RequestParam Region region,
            @RequestParam int page,   // Trang hiện tại
            @RequestParam int size,
            @RequestParam boolean isAscending) { // Số lượng tour mỗi trang
        ///isAscending is true là các tour cu nhat
        // Tính toán offset từ page và size
        int offset = (page - 1) * size;  // offset = (page - 1) * size

        // Gọi service để lấy danh sách tour
        return tourService.getTourByRegion(region, offset, size, isAscending)
                .map(pageData -> {
                    if (pageData.getContent().isEmpty()) {
                        return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy tour
                    }

                    // Tạo response chứa cả thông tin phân trang và danh sách tour
                    Map<String, Object> response = new HashMap<>();
                    response.put("content", pageData.getContent());
                    response.put("totalElements", pageData.getTotalElements());
                    response.put("totalPages", pageData.getTotalPages());
                    response.put("size", pageData.getSize());
                    response.put("number", pageData.getNumber() + 1); // Số trang bắt đầu từ 0 trong PageRequest, nên cần +1

                    return ResponseEntity.ok(response); // Trả về 200 với dữ liệu phân trang
                });
    }


    @GetMapping("/region-order-by-price")
    public Mono<ResponseEntity<Map<String, Object>>> getToursByRegionOrderByPrice(
            @RequestParam Region region,
            @RequestParam int page,   // Trang hiện tại
            @RequestParam int size,
            @RequestParam boolean isAscending) { // Số lượng tour mỗi trang

        // Tính toán offset từ page và size
        int offset = (page - 1) * size;  // offset = (page - 1) * size

        // Gọi service để lấy danh sách tour
        return tourService.findToursByRegionOrderByPrice(region, offset, size, isAscending)
                .map(pageData -> {
                    if (pageData.getContent().isEmpty()) {
                        return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy tour
                    }

                    // Tạo response chứa cả thông tin phân trang và danh sách tour
                    Map<String, Object> response = new HashMap<>();
                    response.put("content", pageData.getContent());
                    response.put("totalElements", pageData.getTotalElements());
                    response.put("totalPages", pageData.getTotalPages());
                    response.put("size", pageData.getSize());
                    response.put("number", pageData.getNumber() + 1); // Số trang bắt đầu từ 0 trong PageRequest, nên cần +1

                    return ResponseEntity.ok(response); // Trả về 200 với dữ liệu phân trang
                });
    }


    @GetMapping("/region-order-by-departure-date")
    public Mono<ResponseEntity<Map<String, Object>>> getToursByRegionOrderByDepartureDate(
            @RequestParam Region region,
            @RequestParam int page,   // Trang hiện tại
            @RequestParam int size,
            @RequestParam boolean isAscending) { // Số lượng tour mỗi trang

        // Tính toán offset từ page và size
        int offset = (page - 1) * size;  // offset = (page - 1) * size

        // Gọi service để lấy danh sách tour
        return tourService.findToursByRegionOrderByDepartureDate(region, offset, size, isAscending)
                .map(pageData -> {
                    if (pageData.getContent().isEmpty()) {
                        return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy tour
                    }

                    // Tạo response chứa cả thông tin phân trang và danh sách tour
                    Map<String, Object> response = new HashMap<>();
                    response.put("content", pageData.getContent());
                    response.put("totalElements", pageData.getTotalElements());
                    response.put("totalPages", pageData.getTotalPages());
                    response.put("size", pageData.getSize());
                    response.put("number", pageData.getNumber() + 1); // Số trang bắt đầu từ 0 trong PageRequest, nên cần +1

                    return ResponseEntity.ok(response); // Trả về 200 với dữ liệu phân trang
                });
    }


    @GetMapping("/by-name")
    public Mono<ResponseEntity<Map<String, Object>>> getToursByNameContainingIgnoreCase(@RequestParam String name,
                                                                                        @RequestParam int page,
                                                                                        @RequestParam int size) {
        return tourService.getToursByNameContainingIgnoreCase(name, page, size)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> {
                    log.error("Error fetching tours by name: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    @GetMapping("/by-date")
    public Mono<ResponseEntity<Flux<TourDTO>>> getToursByDayBetween(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(tourService.getToursByDayBetween(startDate, endDate, page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching tours by date range: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/by-price-between")
    public Mono<ResponseEntity<Flux<TourDTO>>> getToursByPriceBetween(@RequestParam Double minPrice, @RequestParam Double maxPrice, @RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(tourService.getToursByPriceBetween(minPrice, maxPrice, page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching tours by price range: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/by-type")
    public Mono<ResponseEntity<Map<String, Object>>> getToursByTypeTour(@RequestParam TypeTour typeTour,
                                                                        @RequestParam Region region,
                                                                        @RequestParam int page,
                                                                        @RequestParam int size) {
        return tourService.getToursByTypeTour(typeTour, region, page, size)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Error fetching tours by type: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/available")
    public Mono<ResponseEntity<Flux<TourDTO>>> getAvailableTours(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(tourService.getAvailableTours(page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching available tours: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/getFilteredTours")
    public Mono<ResponseEntity<Map<String, Object>>> getLstTourByCriteria(@RequestBody TourFilterCriteriaRequest tourFilterCriteriaRequest, @RequestParam int page,
                                                                          @RequestParam int size) {
        return tourService.findToursByCriteria(tourFilterCriteriaRequest, page, size)
                .map(pageData -> {
                    if (pageData.getContent().isEmpty()) {
                        return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy tour
                    }

                    // Tạo response chứa cả thông tin phân trang và danh sách tour
                    Map<String, Object> response = new HashMap<>();
                    response.put("content", pageData.getContent());
                    response.put("totalElements", pageData.getTotalElements());
                    response.put("totalPages", pageData.getTotalPages());
                    response.put("size", pageData.getSize());
                    response.put("number", pageData.getNumber() + 1); // Số trang bắt đầu từ 0 trong PageRequest, nên cần +1

                    return ResponseEntity.ok(response); // Trả về 200 với dữ liệu phân trang
                });
    }


    @GetMapping(value = "/recommendations-preferences/{customerId}")
    public Mono<ResponseEntity<Flux<TourDTO>>> getRecommendedTourByCriteria(@PathVariable("customerId") Long customerId) {
        return tourService.requestPreferences(customerId)
                .then(Mono.defer(() -> {
                    Flux<TourDTO> tourFlux = eventConsumer.getPreferenceTourStream() // Dữ liệu lưu tru trong Sink
                            .doOnNext(tour -> log.info("Tour emitted for response: {}", tour))
                            .doOnComplete(() -> log.info("Completed emitting tours for customer: {}", customerId))
                            .doOnError(error -> log.error("Error during Flux processing: ", error)); // Log lỗi trong quá trình xử lý Flux

                    // Kiểm tra việc trả về ResponseEntity với Flux đã được xử lý
                    return Mono.just(ResponseEntity.ok(tourFlux));
                }))
                .onErrorResume(error -> {
                    log.error("Error while processing recommendation for customer {}: {}", customerId, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Flux.just(new TourDTO()))); // Có thể trả về một body mặc định nếu có lỗi
                });
    }


    @GetMapping(value = "/recommendations-interactions/{customerId}")
    public Mono<ResponseEntity<Flux<TourDTO>>> getRecommendedTourByInteractions(@PathVariable("customerId") Long customerId) {
        return tourService.requestPreferences(customerId)
                .then(Mono.defer(() -> {
                    Flux<TourDTO> tourFlux = eventConsumer.getInteractionTourStream()
                            .doOnNext(tour -> log.info("Tour emitted for response: {}", tour))
                            .doOnComplete(() -> log.info("Completed emitting tours for customer: {}", customerId))
                            .doOnError(error -> log.error("Error during Flux processing: ", error)); // Log lỗi trong quá trình xử lý Flux

                    // Kiểm tra việc trả về ResponseEntity với Flux đã được xử lý
                    return Mono.just(ResponseEntity.ok(tourFlux));
                }))
                .onErrorResume(error -> {
                    // Thêm log để biết chính xác lỗi xảy ra
                    log.error("Error while processing recommendation for customer {}: {}", customerId, error.getMessage(), error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Flux.just(new TourDTO()))); // Có thể trả về một body mặc định nếu có lỗi
                });
    }

}

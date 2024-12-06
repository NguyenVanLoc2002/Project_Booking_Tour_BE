package com.fit.recommendationservice.controllers;

import com.fit.recommendationservice.dtos.response.PagedResponse;
import com.fit.recommendationservice.dtos.response.TourDTO;
import com.fit.recommendationservice.services.CollaborativeFilteringService;
import com.fit.recommendationservice.services.ContentBasedFilteringService;
import com.fit.recommendationservice.services.HybridRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {
    @Autowired
    private ContentBasedFilteringService filteringService;

    @Autowired
    private CollaborativeFilteringService collaborativeFilteringService;
    @Autowired
    private HybridRecommendationService hybridRecommendationService;

    @GetMapping("/preferences/{customerId}")
    public Mono<ResponseEntity<Map<String, Object>>> recommendTours(@PathVariable Long customerId,
                                                                    @RequestParam int page, @RequestParam int size) {
        return filteringService.recommendToursBasedOnContent(customerId, page, size)
                .map(response -> ResponseEntity.ok().body(response)) // Trả về dữ liệu phân trang
                .defaultIfEmpty(ResponseEntity.notFound().build()); // Trường hợp không có dữ liệu
    }

    @GetMapping("/interaction")
    public Mono<ResponseEntity<Map<String, Object>>> recommendToursByInteraction(
            @RequestParam Long customerId,
            @RequestParam int page,
            @RequestParam int size) {

        return collaborativeFilteringService.recommendToursForUser(customerId, page, size)
                .map(tours -> {
                    // Tạo map để trả về dữ liệu phân trang và danh sách tour
                    Map<String, Object> response = new HashMap<>();
                    response.put("tours", tours);
                    response.put("page", page);
                    response.put("size", size);
                    response.put("totalElements", tours.size());  // Tính số lượng phần tử, bạn có thể thay đổi nếu cần
                    response.put("totalPages", (int) Math.ceil((double) tours.size() / size));  // Tính tổng số trang

                    return ResponseEntity.ok().body(response);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());  // Trường hợp không có dữ liệu
    }

    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<PagedResponse<TourDTO>>> recommendToursHybrid(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return hybridRecommendationService.recommendTours(customerId, page, size)
                .map(ResponseEntity::ok)  // Trả về PagedResponse
                .defaultIfEmpty(ResponseEntity.noContent().build());  // Nếu không có kết quả, trả về No Content
    }


}


package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.ReviewDTO;
import com.fit.tourservice.services.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reviews")
@Slf4j
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public Mono<ResponseEntity<Flux<ReviewDTO>>> getAllReviews(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(reviewService.getAll(page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching all reviews: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ReviewDTO>> addReview(@RequestBody ReviewDTO reviewDTO) {
        return reviewService.addReview(reviewDTO)
                .map(savedReview -> ResponseEntity.status(HttpStatus.CREATED).body(savedReview))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{reviewId}")
    public Mono<ResponseEntity<ReviewDTO>> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO) {
        return reviewService.updateReview(reviewDTO, reviewId)
                .map(updatedReview -> ResponseEntity.ok(updatedReview))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{reviewId}")
    public Mono<ResponseEntity<String>> deleteReview(@PathVariable Long reviewId) {
        return reviewService.deleteReview(reviewId)
                .then(Mono.just(ResponseEntity.ok("Review with ID " + reviewId + " has been deleted.")))
                .onErrorResume(error -> {
                    log.error("Error while deleting Review {}: {}", reviewId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/by-tour")
    public Mono<ResponseEntity<Flux<ReviewDTO>>> getReviewsByTour(@RequestParam Long tourId) {
        return Mono.just(ResponseEntity.ok(reviewService.getReviewsByTourId(tourId)))
                .onErrorResume(e -> {
                    log.error("Error fetching reviews by tour ID: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}


package com.fit.tourservice.services;

import com.fit.tourservice.dtos.ReviewDTO;
import com.fit.tourservice.models.Review;
import com.fit.tourservice.repositories.r2dbc.ReviewRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Mono<ReviewDTO> addReview(ReviewDTO reviewDTO) {
        return Mono.just(reviewDTO)
                .map(ReviewDTO::convertToEntity)
                .flatMap(review -> reviewRepository.save(review))
                .map(ReviewDTO::convertToDTO);
    }

    public Mono<Void> deleteReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> reviewRepository.delete(review));
    }

    public Mono<ReviewDTO> updateReview(ReviewDTO reviewDTO, Long reviewId) {
        return reviewRepository.findById(reviewId)
                .flatMap(review -> {
                    Review updatedReview = ReviewDTO.convertToEntity(reviewDTO);
                    updatedReview.setRewId(review.getRewId());
                    return reviewRepository.save(updatedReview);
                })
                .map(ReviewDTO::convertToDTO);
    }

    public Flux<ReviewDTO> getAll(int page, int size) {
        return reviewRepository.findAll()
                .map(ReviewDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    public Flux<ReviewDTO> getReviewsByTourId(Long tourId) {
        return reviewRepository.findReviewsByTourId(tourId)
                .map(ReviewDTO::convertToDTO);
    }
}

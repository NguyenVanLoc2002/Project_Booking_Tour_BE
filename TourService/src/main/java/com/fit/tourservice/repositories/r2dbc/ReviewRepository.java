package com.fit.tourservice.repositories.r2dbc;

import com.fit.tourservice.models.Review;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface ReviewRepository extends ReactiveCrudRepository<Review, Long> {
    Flux<Review> findReviewsByTourId(Long tourId);
}

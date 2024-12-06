package com.fit.tourservice.dtos;

import com.fit.tourservice.models.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long rewId;
    private Long cusId;
    private Long tourId;
    private int rating;
    private String contentComment;
    private LocalDate reviewDate;

    public static ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getRewId(),
                review.getCusId(),
                review.getTourId(),
                review.getRating(),
                review.getContentComment(),
                review.getReviewDate()
        );
    }

    public static Review convertToEntity(ReviewDTO reviewDTO) {
        return new Review(
                reviewDTO.getRewId(),
                reviewDTO.getCusId(),
                reviewDTO.getTourId(),
                reviewDTO.getRating(),
                reviewDTO.getContentComment(),
                reviewDTO.getReviewDate()
        );
    }
}


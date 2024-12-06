package com.fit.recommendationservice.services;

import com.fit.recommendationservice.builder.MahoutDataModelBuilder;
import com.fit.recommendationservice.dtos.response.CustomerInteractionDTO;
import com.fit.recommendationservice.dtos.response.PagedResponse;
import com.fit.recommendationservice.dtos.response.TourDTO;
import com.fit.recommendationservice.repositories.CustomerInteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class HybridRecommendationService {

    @Autowired
    private CollaborativeFilteringService collaborativeFilteringService;

    @Autowired
    private ContentBasedFilteringService contentBasedFilteringService;

    private final double collaborativeWeight = 0.7;
    private final double contentBasedWeight = 0.3;
    @Autowired
    private CustomerInteractionRepository customerInteractionRepository;
    @Autowired
    private MahoutDataModelBuilder mahoutDataModelBuilder;

    public Mono<PagedResponse<TourDTO>> recommendTours(Long customerId, int page, int size) {
        Mono<List<TourDTO>> collaborativeRecommendations = collaborativeFilteringService
                .recommendToursForUser(customerId, page, size)
                .doOnTerminate(() -> log.info("collaborativeRecommendations completed"))
                .onErrorReturn(List.of()); // Fallback nếu lỗi xảy ra

        Mono<List<TourDTO>> contentBasedRecommendations = contentBasedFilteringService
                .recommendToursBasedOnContent(customerId, page, size)
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    List<TourDTO> tours = (List<TourDTO>) response.get("content");
                    return tours != null ? tours : List.of();
                });

        // Logging Mono when they are subscribed and completed.
        collaborativeRecommendations.subscribe(
                tours -> log.info("Collaborative recommendations: {}", tours),
                error -> log.error("Error in collaborativeRecommendations: ", error)
        );

        contentBasedRecommendations.subscribe(
                tours -> log.info("Content-based recommendations: {}", tours),
                error -> log.error("Error in contentBasedRecommendations: ", error)
        );

        return Mono.zip(collaborativeRecommendations, contentBasedRecommendations)
                .map(tuple -> {
                    List<TourDTO> collaborativeTours = tuple.getT1();
                    List<TourDTO> contentBasedTours = tuple.getT2();

                    // Tính tổng số phần tử trước khi giới hạn
                    int totalElements = collaborativeTours.size() + contentBasedTours.size();
                    log.info("Total number of recommendations: {}", totalElements);

                    // Kết hợp và giới hạn kết quả
                    List<TourDTO> mergedTours = mergeRecommendations(collaborativeTours, contentBasedTours, size);

                    // Tính toán tổng số trang (total pages) nếu cần
                    int totalPages = (int) Math.ceil((double) totalElements / size);
                    boolean last = page >= totalPages - 1;

                    // Trả về kết quả dưới dạng PagedResponse, bao gồm thông tin phân trang
                    return new PagedResponse<>(mergedTours, page, size, totalElements, totalPages, last);
                })
                .doOnTerminate(() -> log.info("Recommendation merging completed"))
                .doOnError(e -> log.error("Error in recommendation merging: ", e));
    }

    private List<TourDTO> mergeRecommendations(List<TourDTO> collaborativeTours, List<TourDTO> contentBasedTours, int size) {
        return Stream.concat(
                        collaborativeTours.stream().map(tour -> adjustWeight(tour, collaborativeWeight)),
                        contentBasedTours.stream().map(tour -> adjustWeight(tour, contentBasedWeight))
                )
                .sorted((t1, t2) -> Double.compare(t2.getRecommendationScore(), t1.getRecommendationScore())) // Sắp xếp giảm dần
                .limit(size) // Giới hạn số lượng tour
                .collect(Collectors.toList());
    }

    private TourDTO adjustWeight(TourDTO tour, double weight) {
        tour.setRecommendationScore(tour.getRecommendationScore() * weight);
        return tour;
    }


    public double calculateCollaborativeScore(TourDTO tour) {
        // Giả sử bạn có một cách lưu trữ các tương tác của người dùng, ví dụ:
        List<CustomerInteractionDTO> interactions = customerInteractionRepository.findByTourId(tour.getTourId())
                .map(CustomerInteractionDTO::convertToDTO)
                .collectList().block();

        // Tính toán điểm cho mỗi tour từ các tương tác của người dùng
        double score = 0.0;

        for (CustomerInteractionDTO interaction : interactions) {
            // Tính toán điểm cho từng tương tác
            score += mahoutDataModelBuilder.calculateScore(interaction);
        }

        // Trả về điểm tổng hợp, có thể điều chỉnh để tạo ra một điểm tổng hợp chính xác hơn
        return score;
    }
}


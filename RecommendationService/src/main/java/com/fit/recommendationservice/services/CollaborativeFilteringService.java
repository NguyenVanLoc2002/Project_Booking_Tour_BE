package com.fit.recommendationservice.services;


import com.fit.recommendationservice.builder.MahoutDataModelBuilder;
import com.fit.recommendationservice.dtos.response.TourDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollaborativeFilteringService {
    @Autowired
    private MahoutDataModelBuilder mahoutDataModelBuilder;

    @Autowired
    private TourServiceClient tourServiceClient;

    public Mono<List<TourDTO>> recommendToursForUser(Long customerId, int page, int size) {
        // Build Data model (Mono<DataModel>)
        return mahoutDataModelBuilder.buildDataModel()
                .flatMap(dataModel -> {
                    try {
                        // Sử dụng thuật toán Pearson Correlation để tính toán độ tương đồng giữa người dùng
                        UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
                        UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, dataModel); // Lấy 10 người dùng gần gũi nhất

                        // Lấy danh sách người dùng gần gũi
                        long[] neighbors = neighborhood.getUserNeighborhood(customerId);
                        log.info("User neighbors for customer {}: {}", customerId, neighbors);

                        // Sử dụng Recommender để đưa ra gợi ý
                        Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
                        List<RecommendedItem> recommendations = recommender.recommend(customerId, 10);
                        log.info("Recommendations for customer {}: {}", customerId, recommendations);

                        // Lấy danh sách các tour được gợi ý
                        List<Long> recommendedTourIds = recommendations.stream()
                                .map(RecommendedItem::getItemID)
                                .collect(Collectors.toList());

                        // Trả về danh sách các tour được gợi ý từ dịch vụ TourServiceClient (bất đồng bộ)
                        return tourServiceClient.findToursByIds(recommendedTourIds, page, size)
                                .map(tourDTOList -> {
                                    // Gán điểm recommendationScore cho mỗi tour
                                    return tourDTOList.stream()
                                            .map(tourDTO -> {
                                                // Tìm item trong danh sách recommendations
                                                RecommendedItem recommendedItem = recommendations.stream()
                                                        .filter(item -> item.getItemID() == tourDTO.getTourId())
                                                        .findFirst()
                                                        .orElse(null);

                                                if (recommendedItem != null) {
                                                    tourDTO.setRecommendationScore(recommendedItem.getValue());
                                                } else {
                                                    tourDTO.setRecommendationScore(0.0); // Nếu không tìm thấy, gán điểm mặc định
                                                }
                                                return tourDTO;
                                            })
                                            .collect(Collectors.toList());
                                });
                    } catch (TasteException e) {
                        // Xử lý ngoại lệ TasteException (nếu có)
                        return Mono.error(new RuntimeException("Error while generating recommendations", e));
                    }
                });
    }



}


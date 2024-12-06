package com.fit.recommendationservice.services;

import com.fit.recommendationservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.recommendationservice.dtos.response.TourDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TourServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;
    private static final String TOUR_SERVICE_URL = "http://APIGATEWAY/api/v1/tours"; // Địa chỉ API của TourService thông qua API GATEWAY

    // Method để gửi yêu cầu filter tours
    public Mono<Map<String, Object>> filterTours(TourFilterCriteriaRequest tourFilterCriteriaRequest, int page, int size) {
        // Sử dụng UriComponentsBuilder để xây dựng URL
        String url = UriComponentsBuilder.fromHttpUrl(TOUR_SERVICE_URL + "/getFilteredTours") // Không cần cổng và localhost
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString(); // Xây dựng URL với các tham số truy vấn

        // Sử dụng WebClient để gửi yêu cầu POST bất đồng bộ
        return webClientBuilder.build()
                .post()
                .uri(url) // Sử dụng URL đã xây dựng
                .bodyValue(tourFilterCriteriaRequest) // Gửi đối tượng TourFilterCriteriaRequest
                .retrieve() // Thực hiện yêu cầu và nhận phản hồi
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                }); // Chuyển đổi kết quả trả về thành Mono<Map>
    }


    public Mono<List<TourDTO>> findToursByIds(List<Long> tourIds, int page, int size) {
        String tourIdsParam = String.join(",", tourIds.stream().map(String::valueOf).collect(Collectors.toList()));
        String url = UriComponentsBuilder.fromHttpUrl(TOUR_SERVICE_URL + "/getToursByIds")
                .queryParam("tourIds", tourIdsParam)
                .queryParam("page", page)
                .queryParam("size", size)
                .toUriString();

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<TourDTO>>() {
                })
                .doOnError(error -> log.error("Error fetching tours: {}", error.getMessage()));
    }
}



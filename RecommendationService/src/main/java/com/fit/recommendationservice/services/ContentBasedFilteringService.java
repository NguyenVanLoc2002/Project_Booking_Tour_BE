package com.fit.recommendationservice.services;

import com.fit.recommendationservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.recommendationservice.dtos.response.TourDTO;
import com.fit.recommendationservice.dtos.response.TourFeatureDTO;
import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContentBasedFilteringService {
    @Autowired
    private TourServiceClient tourServiceClient; // Client giao tiếp với TourService
    @Autowired
    private CustomerPreferenceService preferenceService;

    public Mono<Map<String, Object>> recommendToursBasedOnContent(Long customerId, int page, int size) {
        return preferenceService.getCommonPreferences(customerId)  // Lấy sở thích của khách hàng
                .flatMap(preferences -> {
                    log.info("Preferences: {}", preferences);

                    // Gọi tourServiceClient để lấy danh sách các tour
                    return tourServiceClient.filterTours(preferences, page, size)
                            .doOnError(e -> log.error("Error occurred while calling tourServiceClient: ", e))  // Log khi có lỗi
                            .onErrorResume(throwable -> {
                                // Nếu lỗi là 404, trả về một danh sách trống
                                if (throwable instanceof WebClientResponseException && ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
                                    log.error("Tours not found for preferences: {}", preferences);
                                    // Trả về một response trống thay vì thông báo lỗi
                                    Map<String, Object> emptyResponse = new HashMap<>();
                                    emptyResponse.put("content", new ArrayList<>());  // Danh sách trống
                                    return Mono.just(emptyResponse);
                                }
                                // Trả về lỗi khác nếu có
                                return Mono.error(throwable);
                            })
                            .map(response -> {
                                log.info("Response: {}", response);
                                Object toursObject = response.get("content");

                                // Kiểm tra kiểu dữ liệu an toàn
                                if (toursObject instanceof List<?>) {
                                    List<?> toursList = (List<?>) toursObject;

                                    // Kiểm tra nếu phần tử trong danh sách là Map, tiếp tục xử lý
                                    if (!toursList.isEmpty() && toursList.get(0) instanceof Map<?, ?>) {
                                        List<Map<String, Object>> tours = (List<Map<String, Object>>) toursList;

                                        // Chuyển đổi từ Map thành TourDTO
                                        List<TourDTO> tourDTOList = tours.stream()
                                                .map(this::convertMapToTourDTO) // Chuyển đổi Map thành TourDTO
                                                .collect(Collectors.toList());

                                        // Tính điểm cho từng tour và gán điểm vào mỗi tour
                                        tourDTOList.forEach(tour -> {
                                            double score = calculateScoreForTour(tour, preferences);  // Tính điểm cho tour
                                            tour.setRecommendationScore(score);  // Cập nhật điểm cho tour
                                        });

                                        // Trả lại response đã xử lý
                                        response.put("content", tourDTOList);
                                        return response;
                                    } else {
                                        log.error("Dữ liệu tours không đúng kiểu. Dự liệu phải là List<Map<String, Object>>.");
                                        Map<String, Object> errorResponse = new HashMap<>();
                                        errorResponse.put("message", "Dữ liệu không đúng kiểu");
                                        errorResponse.put("content", new ArrayList<>()); // Trả về một danh sách trống
                                        return errorResponse;
                                    }
                                } else {
                                    log.error("Dữ liệu không chứa danh sách tour hoặc không đúng kiểu");
                                    Map<String, Object> errorResponse = new HashMap<>();
                                    errorResponse.put("message", "Dữ liệu không chứa danh sách tour");
                                    errorResponse.put("content", new ArrayList<>()); // Trả về một danh sách trống
                                    return errorResponse;
                                }
                            });
                });
    }




    private double calculateScoreForTour(TourDTO tour, TourFilterCriteriaRequest preferences) {
        double score = 0.0;

        // Tính điểm dựa trên giá (price)
        score += calculatePriceScore(tour.getPrice(), preferences.getMaxCost());


        // Tính điểm dựa trên địa điểm khởi hành (departureLocation)
        score += calculateDepartureLocationScore(tour.getDepartureLocation(), preferences.getDepartureLocation());

        // Tính điểm dựa trên loại tour (typeTour)
        score += calculateTypeTourScore(tour.getTourFeatureDTO().getTypeTour(), preferences.getTypeTour());

        // Tính điểm dựa trên chỗ ở (accommodationQuality)
        score += calculateAccommodationScore(tour.getTourFeatureDTO().getAccommodationQuality(), preferences.getAccommodationQuality());

        // Tính điểm dựa trên phương tiện di chuyển (transportationMode)
        score += calculateTransportationScore(tour.getTourFeatureDTO().getTransportationMode(), preferences.getTransportationMode());

        return score;
    }

    private double calculatePriceScore(double tourPrice, double maxCost) {
        // Giả sử điểm số cho giá trị càng gần càng tốt
        return Math.max(0, 10 - Math.abs(tourPrice - maxCost)); // Sử dụng công thức đơn giản để tính điểm
    }


    private double calculateDepartureLocationScore(String tourDepartureLocation, String customerDepartureLocation) {
        // Điểm cao nếu địa điểm khởi hành giống nhau
        return tourDepartureLocation.equals(customerDepartureLocation) ? 10 : 0;
    }

    private double calculateTypeTourScore(TypeTour tourType, TypeTour customerType) {
        // Điểm cao nếu loại tour giống nhau
        return tourType.equals(customerType) ? 10 : 0;
    }

    private double calculateAccommodationScore(AccommodationQuality tourAccommodation, AccommodationQuality customerAccommodation) {
        // Điểm cao nếu loại chỗ ở giống nhau
        return tourAccommodation.equals(customerAccommodation) ? 10 : 0;
    }

    private double calculateTransportationScore(TransportationMode tourTransportation, TransportationMode customerTransportation) {
        // Điểm cao nếu phương tiện di chuyển giống nhau
        return tourTransportation.equals(customerTransportation) ? 10 : 0;
    }

    private TourDTO convertMapToTourDTO(Map<String, Object> tourMap) {
        // Tạo TourDTO và gán giá trị từ Map vào các trường của TourDTO
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTourId(((Number) tourMap.get("tourId")).longValue());  // Explicit cast to Long
        tourDTO.setName((String) tourMap.get("name"));
        tourDTO.setPrice(((Number) tourMap.get("price")).doubleValue());  // Explicit cast to Double
        tourDTO.setOldPrice(((Number) tourMap.get("oldPrice")).doubleValue());  // Explicit cast to Double
        tourDTO.setDay((Integer) tourMap.get("day"));
        tourDTO.setNight((Integer) tourMap.get("night"));
        tourDTO.setDestination((String) tourMap.get("destination"));
        tourDTO.setDepartureLocation((String) tourMap.get("departureLocation"));
        tourDTO.setUrlImage((List<String>) tourMap.get("urlImage"));
        tourDTO.setIncludePromotions((Boolean) tourMap.get("includePromotions"));
        tourDTO.setDepartureDate( convertListToLocalDate((List<Integer>) tourMap.get("departureDate")));
        tourDTO.setAvailableSlot((Integer) tourMap.get("availableSlot"));
        tourDTO.setTicketId(((Number) tourMap.get("ticketId")).longValue());
        // Cập nhật các thông tin liên quan đến tính năng tour nếu cần
        if (tourMap.get("tourFeatureDTO") instanceof Map<?, ?>) {
            Map<String, Object> featureMap = (Map<String, Object>) tourMap.get("tourFeatureDTO");
            TourFeatureDTO tourFeature = new TourFeatureDTO();
            tourFeature.setFeatureId(((Number) featureMap.get("featureId")).longValue());  // Explicit cast to Long
            tourFeature.setTourId(((Number) featureMap.get("tourId")).longValue());  // Explicit cast to Long
            tourFeature.setTypeTour(TypeTour.valueOf((String) featureMap.get("typeTour")));
            tourFeature.setRegion(Region.valueOf((String) featureMap.get("region")));
            tourFeature.setAccommodationQuality(AccommodationQuality.valueOf((String) featureMap.get("accommodationQuality")));
            tourFeature.setTransportationMode(TransportationMode.valueOf((String) featureMap.get("transportationMode")));
            tourFeature.setStartDate(convertListToLocalDate((List<Integer>) featureMap.get("startDate")));
            tourFeature.setEndDate(convertListToLocalDate((List<Integer>) featureMap.get("endDate")));

            tourDTO.setTourFeatureDTO(tourFeature);
        }

        return tourDTO;
    }


    private LocalDate convertListToLocalDate(List<Integer> dateList) {
        if (dateList != null && dateList.size() >= 3) {
            int year = dateList.get(0);   // Lấy năm từ vị trí đầu tiên
            int month = dateList.get(1);  // Lấy tháng từ vị trí thứ hai
            int day = dateList.get(2);    // Lấy ngày từ vị trí cuối cùng

            // Kiểm tra ngày tháng năm hợp lệ trước khi tạo LocalDate
            try {
                return LocalDate.of(year, month, day);  // Tạo LocalDate nếu ngày hợp lệ
            } catch (DateTimeException e) {
                log.error("Ngày tháng năm không hợp lệ: {}/{}/{}", day, month, year);
                return null;  // Trả về null hoặc có thể ném exception tùy vào yêu cầu
            }
        }
        return null; // Trả về null nếu dateList không hợp lệ hoặc thiếu dữ liệu
    }

}


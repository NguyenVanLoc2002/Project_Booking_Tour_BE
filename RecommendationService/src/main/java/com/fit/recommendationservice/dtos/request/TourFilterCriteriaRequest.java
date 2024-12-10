package com.fit.recommendationservice.dtos.request;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TourFilterCriteriaRequest {
    private Double maxCost;
    private LocalDate startDate;
    private Integer maxDuration;
    private String departureLocation;
    private TypeTour typeTour;
    private AccommodationQuality accommodationQuality;
    private Region region;
    private TransportationMode transportationMode;
}

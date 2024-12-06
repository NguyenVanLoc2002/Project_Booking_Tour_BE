package com.fit.recommendationservice.dtos.response;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourFeatureDTO {
    private Long featureId;
    private Long tourId;
    private TypeTour typeTour;
    private Region region;
    private AccommodationQuality accommodationQuality;
    private TransportationMode transportationMode;
    private LocalDate startDate;
    private LocalDate endDate;
}


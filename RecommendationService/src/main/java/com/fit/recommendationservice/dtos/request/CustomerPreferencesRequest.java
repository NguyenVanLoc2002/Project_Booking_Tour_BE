package com.fit.recommendationservice.dtos.request;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreferencesRequest {
    private Long cusId;
    private Double maxCost;
    private int maxDuration;
    private String departureLocation;
    private LocalDate startDate;
    private TypeTour typeTour; // Enum
    private Region region; // Enum
    private AccommodationQuality accommodationQuality; // Enum
    private TransportationMode transportationMode;
}

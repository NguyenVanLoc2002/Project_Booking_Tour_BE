package com.fit.paymentservice.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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


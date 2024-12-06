package com.fit.tourservice.dtos.request;

import com.fit.tourservice.enums.AccommodationQuality;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TransportationMode;
import com.fit.tourservice.enums.TypeTour;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TourFilterCriteriaRequest {
    private double maxCost;
    private LocalDate startDate;
    private int maxDuration;
    private String departureLocation;
    private TypeTour typeTour;
    private AccommodationQuality accommodationQuality;
    private Region region;
    private TransportationMode transportationMode;

//    public Integer getTypeTourValue() {
//        return typeTour != null ? typeTour.getValue() : null;
//    }
//
//    public Integer getAccommodationQualityValue() {
//        return accommodationQuality != null ? accommodationQuality.getValue() : null;
//    }
//
//    public Integer getRegionValue() {
//        return region != null ? region.getValue() : null;
//    }
//
//    public Integer getTransportationModeValue() {
//        return transportationMode != null ? transportationMode.getValue() : null;
//    }
}

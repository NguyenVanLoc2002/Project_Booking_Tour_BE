package com.fit.tourservice.models;

import com.fit.tourservice.enums.AccommodationQuality;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TransportationMode;
import com.fit.tourservice.enums.TypeTour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tour_feature")
public class TourFeature {
    @Id
    private Long featureId;
    private Long tourId;
    private TypeTour typeTour;
    private Region region;
    private AccommodationQuality accommodationQuality;
    private TransportationMode transportationMode;
    private LocalDate startDate;
    private LocalDate endDate;
}

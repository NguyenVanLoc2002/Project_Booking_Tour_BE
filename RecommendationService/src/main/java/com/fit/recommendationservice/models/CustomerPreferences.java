package com.fit.recommendationservice.models;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("customer_preferences")
public class CustomerPreferences {
    @Id
    private Long preId;
    private Long cusId;
    private Double price;
    private int maxDuration;
    private String departureLocation;
    private LocalDate startDate;
    private TypeTour typeTour; // Enum
    private Region region; // Enum
    private AccommodationQuality accommodationQuality; // Enum
    private TransportationMode transportationMode;
}

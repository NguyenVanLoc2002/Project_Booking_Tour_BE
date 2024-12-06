package com.fit.recommendationservice.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TourDTO {
    private Long tourId;
    private String name;
    private double price;
    private double oldPrice;
    private int day;
    private int night;
    private String destination;
    private String departureLocation;
    private List<String> urlImage;
    private boolean includePromotions;
    private TourFeatureDTO tourFeatureDTO;
    private LocalDate departureDate;
    private int availableSlot;
    private double recommendationScore;
}

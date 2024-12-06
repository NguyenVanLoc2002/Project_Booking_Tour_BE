package com.fit.tourservice.dtos;

import com.fit.tourservice.enums.AccommodationQuality;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TransportationMode;
import com.fit.tourservice.enums.TypeTour;
import com.fit.tourservice.models.TourFeature;
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

    // Phương thức chuyển từ Entity sang DTO
    public static TourFeatureDTO convertToDTO(TourFeature tourFeature) {
        TourFeatureDTO tourFeatureDTO = new TourFeatureDTO();
        tourFeatureDTO.setFeatureId(tourFeature.getFeatureId());
        tourFeatureDTO.setTourId(tourFeature.getTourId());
        tourFeatureDTO.setTypeTour(tourFeature.getTypeTour());
        tourFeatureDTO.setRegion(tourFeature.getRegion());
        tourFeatureDTO.setAccommodationQuality(tourFeature.getAccommodationQuality());
        tourFeatureDTO.setTransportationMode(tourFeature.getTransportationMode());
        tourFeatureDTO.setStartDate(tourFeature.getStartDate());
        tourFeatureDTO.setEndDate(tourFeature.getEndDate());
        return tourFeatureDTO;
    }


    // Phương thức chuyển từ DTO sang Entity
    public static TourFeature convertToEntity(TourFeatureDTO tourFeatureDTO) {
        TourFeature tourFeature = new TourFeature();
        tourFeature.setFeatureId(tourFeatureDTO.getFeatureId());
        tourFeature.setTourId(tourFeatureDTO.getTourId());
        tourFeature.setTypeTour(tourFeatureDTO.getTypeTour());
        tourFeature.setRegion(tourFeatureDTO.getRegion());
        tourFeature.setAccommodationQuality(tourFeatureDTO.getAccommodationQuality());
        tourFeature.setTransportationMode(tourFeatureDTO.getTransportationMode());
        tourFeature.setStartDate(tourFeatureDTO.getStartDate());
        tourFeature.setEndDate(tourFeatureDTO.getEndDate());
        return tourFeature;
    }

}


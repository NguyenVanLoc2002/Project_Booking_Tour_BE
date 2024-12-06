package com.fit.tourservice.dtos;

import com.fit.tourservice.models.ServiceTour;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTourDTO {
    private Long serviceId;
    private Long tourId;
    private String includedService;
    private String excludedService;

    public static ServiceTourDTO convertToDTO(ServiceTour serviceTour) {
        return new ServiceTourDTO(
                serviceTour.getServiceId(),
                serviceTour.getTourId(),
                serviceTour.getIncludedService(),
                serviceTour.getExcludedService()
        );
    }

    public static ServiceTour convertToEntity(ServiceTourDTO serviceTourDTO) {
        return new ServiceTour(
                serviceTourDTO.getServiceId(),
                serviceTourDTO.getTourId(),
                serviceTourDTO.getIncludedService(),
                serviceTourDTO.getExcludedService()
        );
    }
}


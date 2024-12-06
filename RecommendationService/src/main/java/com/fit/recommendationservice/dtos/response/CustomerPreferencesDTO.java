package com.fit.recommendationservice.dtos.response;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import com.fit.recommendationservice.models.CustomerPreferences;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerPreferencesDTO {
    private Long preId;
    private Long cusId;
    private Double price;
    private int maxDuration;
    private LocalDate startDate;
    private TypeTour typeTour; // Enum
    private Region region; // Enum
    private AccommodationQuality accommodationQuality; // Enum
    private TransportationMode transportationMode;

    public static CustomerPreferences convertToEntity(CustomerPreferencesDTO customerPreferencesDTO) {
        CustomerPreferences customerPreferences = new CustomerPreferences();
        customerPreferences.setPreId(customerPreferencesDTO.getPreId());
        customerPreferences.setCusId(customerPreferencesDTO.getCusId());
        customerPreferences.setPrice(customerPreferencesDTO.getPrice());
        customerPreferences.setMaxDuration(customerPreferences.getMaxDuration());
        customerPreferences.setTypeTour(customerPreferencesDTO.getTypeTour());
        customerPreferences.setRegion(customerPreferencesDTO.getRegion());
        customerPreferences.setAccommodationQuality(customerPreferencesDTO.getAccommodationQuality());
        customerPreferences.setTransportationMode(customerPreferencesDTO.getTransportationMode());
        return customerPreferences;
    }

    public static CustomerPreferencesDTO convertToDTO(CustomerPreferences customerPreferences) {
        CustomerPreferencesDTO customerPreferencesDTO = new CustomerPreferencesDTO();
        customerPreferencesDTO.setPreId(customerPreferences.getPreId());
        customerPreferencesDTO.setCusId(customerPreferences.getCusId());
        customerPreferencesDTO.setPrice(customerPreferences.getPrice());
        customerPreferencesDTO.setMaxDuration(customerPreferences.getMaxDuration());
        customerPreferencesDTO.setStartDate(customerPreferences.getStartDate());
        customerPreferencesDTO.setTypeTour(customerPreferences.getTypeTour());
        customerPreferencesDTO.setRegion(customerPreferences.getRegion());
        customerPreferencesDTO.setAccommodationQuality(customerPreferences.getAccommodationQuality());
        customerPreferencesDTO.setTransportationMode(customerPreferences.getTransportationMode());
        return customerPreferencesDTO;
    }
}

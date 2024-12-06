package com.fit.tourservice.dtos;

import com.fit.tourservice.enums.WeatherCondition;
import com.fit.tourservice.models.Itineraries;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItinerariesDTO {
    private Long itinerId;
    private Long tourId;
    private int dayNumber;
    private String title;
    private String description;
    private float temperature;
    private WeatherCondition weatherCondition;
    private String activity;

    public static ItinerariesDTO convertToDTO(Itineraries itineraries) {
        if (itineraries == null) {
            return null;
        }
        return new ItinerariesDTO(
                itineraries.getItinerId(),
                itineraries.getTourId(),
                itineraries.getDayNumber(),
                itineraries.getTitle(),
                itineraries.getDescription(),
                itineraries.getTemperature(),
                itineraries.getWeatherCondition(),
                itineraries.getActivity()
        );
    }

    public static Itineraries convertToEntity(ItinerariesDTO itinerariesDTO) {
        if (itinerariesDTO == null) {
            return null;
        }
        Itineraries itineraries = new Itineraries();
        itineraries.setItinerId(itinerariesDTO.getItinerId());
        itineraries.setTourId(itinerariesDTO.getTourId());
        itineraries.setDayNumber(itinerariesDTO.getDayNumber());
        itineraries.setTitle(itinerariesDTO.getTitle());
        itineraries.setDescription(itinerariesDTO.getDescription());
        itineraries.setTemperature(itinerariesDTO.getTemperature());
        itineraries.setWeatherCondition(itinerariesDTO.getWeatherCondition());
        itineraries.setActivity(itinerariesDTO.getActivity());
        return itineraries;
    }

}

package com.fit.tourservice.models;

import com.fit.tourservice.enums.WeatherCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "itineraries")
public class Itineraries {
    @Id
    private Long itinerId;
    private Long tourId;
    private int dayNumber;
    private String title;
    private String description;
    private float temperature;
    private WeatherCondition weatherCondition;
    private String activity;
}

package com.fit.recommendationservice.dtos.request;

import com.fit.recommendationservice.enums.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInteractionRequest {
    private Long cusId;
    private Long tourId;
    private InteractionType interactionType; // Enum
    private LocalDate interactionDate;
}

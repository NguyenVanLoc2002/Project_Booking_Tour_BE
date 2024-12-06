package com.fit.recommendationservice.models;

import com.fit.recommendationservice.enums.InteractionType;
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
@Table(name = "customer_interaction")
public class CustomerInteraction {
    @Id
    private Long interactionId;
    private Long cusId;
    private Long tourId;
    private InteractionType interactionType; // Enum
    private LocalDate interactionDate;
}

package com.fit.recommendationservice.dtos.response;

import com.fit.recommendationservice.enums.InteractionType;
import com.fit.recommendationservice.models.CustomerInteraction;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerInteractionDTO {
    private Long interactionId;
    private Long cusId;
    private Long tourId;
    private InteractionType interactionType; // Enum
    private LocalDate interactionDate;

    public static CustomerInteraction convertToEntity(CustomerInteractionDTO customerInteractionDTO) {
        CustomerInteraction customerInteraction = new CustomerInteraction();
        customerInteraction.setInteractionId(customerInteractionDTO.getInteractionId());
        customerInteraction.setCusId(customerInteractionDTO.getCusId());
        customerInteraction.setTourId(customerInteractionDTO.getTourId());
        customerInteraction.setInteractionType(customerInteractionDTO.getInteractionType());
        customerInteraction.setInteractionDate(customerInteractionDTO.getInteractionDate());
        return customerInteraction;
    }

    public static CustomerInteractionDTO convertToDTO(CustomerInteraction customerInteraction) {
        CustomerInteractionDTO customerInteractionDTO = new CustomerInteractionDTO();
        customerInteractionDTO.setInteractionId(customerInteraction.getInteractionId());
        customerInteractionDTO.setCusId(customerInteraction.getCusId());
        customerInteractionDTO.setTourId(customerInteraction.getTourId());
        customerInteractionDTO.setInteractionType(customerInteraction.getInteractionType());
        customerInteractionDTO.setInteractionDate(customerInteraction.getInteractionDate());
        return customerInteractionDTO;
    }
}

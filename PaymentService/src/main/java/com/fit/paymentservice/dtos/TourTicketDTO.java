package com.fit.paymentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourTicketDTO {
    private Long ticketId;
    private Long tourId;
    private LocalDate departureDate;
    private String departureLocation;
    private int availableSlot;

}


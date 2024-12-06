package com.fit.tourservice.models;

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
@Table(name = "tour_tickets")
public class TourTicket {
    @Id
    private Long ticketId;
    private Long tourId;
        private LocalDate departureDate;
    //    Dia diem khoi hanh
    private String departureLocation;
    private int availableSlot;
}

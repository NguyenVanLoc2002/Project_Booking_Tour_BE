package com.fit.tourservice.dtos;

import com.fit.tourservice.models.TourTicket;
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

    // Phương thức chuyển đổi từ TourTicket sang TourTicketDTO
    public static TourTicketDTO convertToDTO(TourTicket tourTicket) {
        if (tourTicket == null) {
            return null;
        }
        return new TourTicketDTO(
                tourTicket.getTicketId(),
                tourTicket.getTourId(),
                tourTicket.getDepartureDate(),
                tourTicket.getDepartureLocation(),
                tourTicket.getAvailableSlot()
        );
    }

    // Phương thức chuyển đổi từ TourTicketDTO sang TourTicket
    public static TourTicket convertToEntity(TourTicketDTO tourTicketDTO) {
        if (tourTicketDTO == null) {
            return null;
        }
        return new TourTicket(
                tourTicketDTO.getTicketId(),
                tourTicketDTO.getTourId(),
                tourTicketDTO.getDepartureDate(),
                tourTicketDTO.getDepartureLocation(),
                tourTicketDTO.getAvailableSlot()
        );
    }
}


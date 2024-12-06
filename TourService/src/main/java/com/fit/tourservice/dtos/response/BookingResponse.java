package com.fit.tourservice.dtos.response;

import com.fit.tourservice.enums.StatusBooking;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private UUID bookingId;
    private LocalDate bookingDate;
    private StatusBooking statusBooking;
    private double totalAmount;
    private int quantity;
    private boolean isAvailable;
}

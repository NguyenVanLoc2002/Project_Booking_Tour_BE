package com.fit.paymentservice.dtos.response;

import com.fit.paymentservice.enums.StatusBooking;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private String bookingId;
    private LocalDate bookingDate;
    private StatusBooking statusBooking;
    private double totalAmount;
    private int quantity;
    private boolean isAvailable;
}

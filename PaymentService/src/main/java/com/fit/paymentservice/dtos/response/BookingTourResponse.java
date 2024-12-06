package com.fit.paymentservice.dtos.response;

import com.fit.paymentservice.dtos.BookingDTO;
import com.fit.paymentservice.dtos.TourDTO;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingTourResponse {
    private BookingDTO bookingDTO;
    private TourDTO tourDTO;
}

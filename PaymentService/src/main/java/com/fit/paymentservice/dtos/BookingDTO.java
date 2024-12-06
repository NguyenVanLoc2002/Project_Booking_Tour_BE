package com.fit.paymentservice.dtos;

import com.fit.paymentservice.enums.StatusBooking;
import com.fit.paymentservice.models.Booking;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private String bookingId;
    private Long customerId;       // ID khách hàng nếu là khách đã đăng ký, null nếu là khách vãng lai
    private Long tourId;
    private Long ticketId;
    private LocalDate bookingDate;
    private StatusBooking statusBooking;
    private double totalAmount;
    private int quantity;
    private int adults;
    private int children;
    private int toddlers;
    private int infants;

    // Chỉ dùng cho khách vãng lai
    private String email;
    private String userName;
    private String phoneNumber;
    private String city;
    private String district;
    private String ward;
    private String address;

    // Chuyển đổi từ Booking sang BookingDTO
    public static BookingDTO convertToDto(Booking booking) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(booking.getBookingId());
        bookingDTO.setCustomerId(booking.getCustomerId());
        bookingDTO.setTourId(booking.getTourId());
        bookingDTO.setTicketId(booking.getTicketId());
        bookingDTO.setBookingDate(booking.getBookingDate());
        bookingDTO.setStatusBooking(booking.getStatusBooking());
        bookingDTO.setTotalAmount(booking.getTotalAmount());
        bookingDTO.setQuantity(booking.getQuantity());
        bookingDTO.setAdults(booking.getAdults());
        bookingDTO.setChildren(booking.getChildren());
        bookingDTO.setToddlers(booking.getToddlers());
        bookingDTO.setInfants(booking.getInfants());


        bookingDTO.setEmail(booking.getEmail());
        bookingDTO.setUserName(booking.getUserName());
        bookingDTO.setPhoneNumber(booking.getPhoneNumber());
        bookingDTO.setCity(booking.getCity());
        bookingDTO.setDistrict(booking.getDistrict());
        bookingDTO.setWard(booking.getWard());
        bookingDTO.setAddress(booking.getAddress());

        return bookingDTO;
    }

    // Chuyển đổi từ BookingDTO sang Booking
    public static Booking convertToEntity(BookingDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setBookingId(bookingDTO.getBookingId());
        booking.setCustomerId(bookingDTO.getCustomerId());
        booking.setTourId(bookingDTO.getTourId());
        booking.setTicketId(bookingDTO.getTicketId());
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setStatusBooking(bookingDTO.getStatusBooking());
        booking.setTotalAmount(bookingDTO.getTotalAmount());
        booking.setQuantity(bookingDTO.getQuantity());
        booking.setAdults(bookingDTO.getAdults());
        booking.setChildren(bookingDTO.getChildren());
        booking.setToddlers(bookingDTO.getToddlers());
        booking.setInfants(bookingDTO.getInfants());

        // Nếu là khách vãng lai

        booking.setEmail(bookingDTO.getEmail());
        booking.setUserName(bookingDTO.getUserName());
        booking.setPhoneNumber(bookingDTO.getPhoneNumber());
        booking.setCity(bookingDTO.getCity());
        booking.setDistrict(bookingDTO.getDistrict());
        booking.setWard(bookingDTO.getWard());
        booking.setAddress(bookingDTO.getAddress());

        return booking;
    }
}


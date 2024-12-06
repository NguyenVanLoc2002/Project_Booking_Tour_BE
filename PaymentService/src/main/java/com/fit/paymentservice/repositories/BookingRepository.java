package com.fit.paymentservice.repositories;

import com.fit.paymentservice.models.Booking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface BookingRepository extends ReactiveCrudRepository<Booking, String> {
    @Override
    Mono<Booking> findById(String String);

    Flux<Booking> findBookingByCustomerId(Long customerId);

    // Phương thức thêm booking mới
    @Query("INSERT INTO booking (booking_id, tour_id, ticket_id, booking_date, status_booking, total_amount, quantity, adults, children, toddlers, infants, customer_id, email, user_name, phone_number, city, district, ward, address) " +
            "VALUES (:bookingId, :tourId, :ticketId, :bookingDate, :statusBooking, :totalAmount, :quantity, :adults, :children, :toddlers, :infants, :customerId, :email, :userName, :phoneNumber, :city, :district, :ward, :address) " +
            "RETURNING *")
    Mono<Booking> saveBooking(@Param("bookingId") String bookingId,
                              @Param("tourId") Long tourId,
                              @Param("ticketId") Long ticketId,
                              @Param("bookingDate") LocalDate bookingDate,
                              @Param("statusBooking") String statusBooking,
                              @Param("totalAmount") Double totalAmount,
                              @Param("quantity") Integer quantity,
                              @Param("adults") Integer adults,
                              @Param("children") Integer children,
                              @Param("toddlers") Integer toddlers,
                              @Param("infants") Integer infants,
                              @Param("customerId") Long customerId,
                              @Param("email") String email,
                              @Param("userName") String userName,
                              @Param("phoneNumber") String phoneNumber,
                              @Param("city") String city,
                              @Param("district") String district,
                              @Param("ward") String ward,
                              @Param("address") String address);

}

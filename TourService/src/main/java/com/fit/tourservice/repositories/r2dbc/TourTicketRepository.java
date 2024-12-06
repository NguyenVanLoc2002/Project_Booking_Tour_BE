package com.fit.tourservice.repositories.r2dbc;

import com.fit.tourservice.models.TourTicket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EnableR2dbcRepositories
public interface TourTicketRepository extends ReactiveCrudRepository<TourTicket, Long> {
    @Query("Select * from tour_tickets where tour_id = :tourId AND departure_date > CURRENT_DATE")
    Flux<TourTicket> findTourTicketsByTourId(@Param("tourId") Long tourId);

    @Query("SELECT * FROM tour_tickets WHERE tour_id = :tourId AND departure_date > CURRENT_DATE ORDER BY departure_date ASC LIMIT 1")
    Mono<TourTicket> findClosestTourTicketByTourId(@Param("tourId") Long tourId);

    Mono<TourTicket> findByTicketId(@Param("ticketId") Long ticketId);

}

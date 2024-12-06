package com.fit.recommendationservice.repositories;

import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import com.fit.recommendationservice.models.CustomerPreferences;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface CustomerPreferencesRepository extends ReactiveCrudRepository<CustomerPreferences, Long> {
    Flux<CustomerPreferences> findByCusId(Long cusId);

    @Query("SELECT price FROM customer_preferences WHERE cus_id = :customerId GROUP BY price ORDER BY start_date DESC LIMIT 1")
    Mono<Double> findLatestPrice(@Param("customerId") Long customerId);

    @Query("SELECT max_duration FROM customer_preferences WHERE cus_id = :customerId GROUP BY max_duration ORDER BY start_date DESC LIMIT 1")
    Mono<Integer> findLatestDuration(@Param("customerId") Long customerId);

    @Query("SELECT departure_location FROM customer_preferences WHERE cus_id = :customerId GROUP BY departure_location ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<String> findPopularDepartureLocation(@Param("customerId") Long customerId);

    @Query("SELECT type_tour FROM customer_preferences WHERE cus_id = :customerId GROUP BY type_tour ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<String> findPopularTypeTour(@Param("customerId") Long customerId);

    @Query("SELECT region FROM customer_preferences WHERE cus_id = :customerId GROUP BY region ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<String> findPopularRegion(Long customerId);

    @Query("SELECT accommodation_quality FROM customer_preferences WHERE cus_id = :customerId GROUP BY accommodation_quality ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<String> findPopularAccommodationQuality(Long customerId);

    @Query("SELECT transportation_mode FROM customer_preferences WHERE cus_id = :customerId GROUP BY transportation_mode ORDER BY COUNT(*) DESC LIMIT 1")
    Mono<String> findPopularTransportationMode(Long customerId);


    @Query("INSERT INTO customer_preferences (cus_id, price, max_duration, departure_location, start_date, type_tour, region, accommodation_quality, transportation_mode) " +
            "VALUES (:cusId, :maxCost, :maxDuration, :departureLocation, :startDate, :typeTour, :region, :accommodationQuality, :transportationMode) ")
    Mono<CustomerPreferences> insertCustomerPreferences(
            @Param("cusId") Long cusId,
            @Param("maxCost") Double maxCost,
            @Param("maxDuration") int maxDuration,
            @Param("departureLocation") String departureLocation,
            @Param("startDate") LocalDate startDate,
            @Param("typeTour") TypeTour typeTour,
            @Param("region") Region region,
            @Param("accommodationQuality") AccommodationQuality accommodationQuality,
            @Param("transportationMode") TransportationMode transportationMode);
}

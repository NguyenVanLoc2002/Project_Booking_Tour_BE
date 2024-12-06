package com.fit.tourservice.repositories.r2dbc;

import com.fit.tourservice.enums.AccommodationQuality;
import com.fit.tourservice.enums.Region;
import com.fit.tourservice.enums.TransportationMode;
import com.fit.tourservice.enums.TypeTour;
import com.fit.tourservice.models.Tour;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface TourRepository extends ReactiveCrudRepository<Tour, Long> {

    Mono<Tour> findById(Long id);

    @Query("""
                        SELECT DISTINCT T.* 
                        FROM tours T
                        JOIN tour_feature TF ON T.tour_id = TF.tour_id
                        JOIN tour_tickets TT ON T.tour_id = TT.tour_id
                        WHERE TT.available_slot > 0
                          AND (:maxCost IS NULL OR T.price <= :maxCost)
                          AND (:maxDuration IS NULL OR DATEDIFF(TF.end_date, TF.start_date) <= :maxDuration)
                          AND (:departureLocation IS NULL OR TT.departure_location = :departureLocation)
                          AND (:startDate IS NULL OR TT.departure_date >= :startDate)
                          AND (:typeTour IS NULL OR TF.type_tour = :typeTour)
                          AND (:accommodationQuality IS NULL OR TF.accommodation_quality = :accommodationQuality)
                          AND (:region IS NULL OR TF.region = :region)
                          AND (:transportationMode IS NULL OR TF.transportation_mode = :transportationMode)
                        LIMIT :limit OFFSET :offset
            """)
    Flux<Tour> findToursByCriteria(@Param("maxCost") double maxCost,
                                      @Param("maxDuration") int maxDuration,
                                      @Param("departureLocation") String departureLocation,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("typeTour") TypeTour typeTour,
                                      @Param("accommodationQuality") AccommodationQuality accommodationQuality,
                                      @Param("region") Region region,
                                      @Param("transportationMode") TransportationMode transportationMode,
                                      @Param("limit") int limit, @Param("offset") int offset);

    @Query("""
                    SELECT COUNT(DISTINCT T.tour_id)
                    FROM tours T
                    JOIN tour_feature TF ON T.tour_id = TF.tour_id
                    JOIN tour_tickets TT ON T.tour_id = TT.tour_id
                    WHERE TT.available_slot > 0
                      AND (:maxCost IS NULL OR T.price <= :maxCost)
                      AND (:maxDuration IS NULL OR DATEDIFF(TF.end_date, TF.start_date) <= :maxDuration)
                      AND (:departureLocation IS NULL OR TT.departure_location = :departureLocation)
                      AND (:startDate IS NULL OR TT.departure_date >= :startDate)
                      AND (:typeTour IS NULL OR TF.type_tour = :typeTour)
                      AND (:accommodationQuality IS NULL OR TF.accommodation_quality = :accommodationQuality)
                      AND (:region IS NULL OR TF.region = :region)
                      AND (:transportationMode IS NULL OR TF.transportation_mode = :transportationMode)
            """)
    Mono<Long> countToursByCriteria(@Param("maxCost") Double maxCost,
                                    @Param("maxDuration") Integer maxDuration,
                                    @Param("departureLocation") String departureLocation,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("typeTour") TypeTour typeTour,
                                    @Param("accommodationQuality") AccommodationQuality accommodationQuality,
                                    @Param("region") Region region,
                                    @Param("transportationMode") TransportationMode transportationMode);


    Flux<Tour> findByTourIdIn(List<Long> tourIds);


    @Query("SELECT COUNT(*) FROM tours WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Mono<Long> countToursByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT * FROM tours WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%')) LIMIT :limit OFFSET :offset")
    Flux<Tour> findToursByNameContainingIgnoreCaseWithPagination(@Param("name") String name, @Param("limit") int limit, @Param("offset") int offset);


    @Query("SELECT * FROM tours T " + "JOIN tour_feature TF ON T.tour_id = TF.tour_id " + "WHERE TF.start_date >= :startDate AND " + "TF.end_date <= :endDate")
    Flux<Tour> findToursByDayBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Flux<Tour> findToursByPriceBetween(Double minPrice, Double maxPrice);

    @Query("SELECT * FROM tours T " +
            "JOIN tour_feature TF ON T.tour_id = TF.tour_id " +
            "WHERE TF.type_tour = :typeTour AND TF.region = :region " +
            "LIMIT :limit OFFSET :offset")
    Flux<Tour> findToursByTypeTour(@Param("typeTour") TypeTour typeTour,
                                   @Param("region") Region region,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);

    @Query("SELECT COUNT(*) FROM tours T " +
            "JOIN tour_feature TF ON T.tour_id = TF.tour_id " +
            "WHERE TF.type_tour = :typeTour AND TF.region = :region")
    Mono<Long> countToursByTypeTour(@Param("typeTour") TypeTour typeTour,
                                    @Param("region") Region region);



    @Query("SELECT T.* " + "FROM tours T " + "JOIN tour_feature TF ON T.tour_id = TF.tour_id " + "JOIN tour_tickets TT ON T.tour_id = TT.tour_id " + "WHERE TF.start_date >= CURRENT_DATE " + "AND TT.departure_date >= CURRENT_DATE " + "AND TT.available_slot > 0 " + "GROUP BY T.tour_id " + "HAVING MIN(TT.departure_date) >= CURRENT_DATE")
    Flux<Tour> findAvailableTours();


    @Query("""
            SELECT t.*
            FROM tours t
            JOIN (
                SELECT tour_id, MAX(departure_date) AS latestDepartureDate, available_slot
                FROM tour_tickets
                WHERE departure_date > CURRENT_DATE
                      AND available_slot > 0
                GROUP BY tour_id
            ) tt ON t.tour_id = tt.tour_id
            JOIN tour_feature tf ON t.tour_id = tf.tour_id
            WHERE tf.region = :region
            ORDER BY tt.latestDepartureDate DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Tour> findToursWithLatestDepartureByRegion(@Param("region") Region region, @Param("limit") int limit, @Param("offset") int offset);


    @Query("""
            SELECT t.*
            FROM tours t
            JOIN (
                SELECT tour_id, MIN(departure_date) AS earliestDepartureDate, available_slot
                FROM tour_tickets
                WHERE departure_date > CURRENT_DATE
                      AND available_slot > 0
                GROUP BY tour_id
            ) tt ON t.tour_id = tt.tour_id
            JOIN tour_feature tf ON t.tour_id = tf.tour_id
            WHERE tf.region = :region
            ORDER BY tt.earliestDepartureDate ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<Tour> findToursWithEarliestDepartureByRegion(@Param("region") Region region, @Param("limit") int limit, @Param("offset") int offset);

}

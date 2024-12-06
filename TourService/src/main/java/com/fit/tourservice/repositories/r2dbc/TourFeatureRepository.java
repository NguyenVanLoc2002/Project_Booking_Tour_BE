package com.fit.tourservice.repositories.r2dbc;

import com.fit.tourservice.enums.Region;
import com.fit.tourservice.models.TourFeature;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TourFeatureRepository extends ReactiveCrudRepository<TourFeature, Long> {
    Mono<TourFeature> findById(Long tourId);

    @Query("""
       SELECT DISTINCT tf.*
       FROM tour_feature tf
       JOIN tour_tickets tt ON tf.tour_id = tt.tour_id
       WHERE tf.region = :region AND tt.departure_date > CURRENT_DATE
       ORDER BY tf.start_date ASC
       LIMIT :limit OFFSET :offset
       """)
    Flux<TourFeature> findAllByRegionAndStartDateAfter(
            @Param("region") Region region,
            @Param("limit") int limit,   // Định nghĩa limit cho số lượng bản ghi
            @Param("offset") int offset  // Định nghĩa offset cho phân trang
    );

    @Query("""
            SELECT DISTINCT tf.* 
            FROM tour_feature tf
            JOIN tour_tickets tt ON tf.tour_id = tt.tour_id
            WHERE tf.region = :region AND tt.departure_date > CURRENT_DATE
            ORDER BY tf.start_date DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<TourFeature> findAllByRegionAndStartDateBefore(
            @Param("region") Region region,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query("SELECT COUNT(DISTINCT tf.tour_id) FROM tour_feature tf " +
            "JOIN tour_tickets tt ON tf.tour_id = tt.tour_id " +
            "WHERE tf.region = :region AND tt.departure_date > CURRENT_DATE")
    Mono<Long> countToursByRegionAndDepartureDateAfter(@Param("region") Region region);



    @Query("""
       SELECT DISTINCT tf.* 
       FROM tour_feature tf
       JOIN tours t ON tf.tour_id = t.tour_id
       JOIN tour_tickets tt ON t.tour_id = tt.tour_id
       WHERE tf.region = :region AND tt.departure_date > CURRENT_DATE
       ORDER BY t.price DESC 
       LIMIT :limit OFFSET :offset
       """)
    Flux<TourFeature> findAllByRegionAndDepartureDateAfterOrderByPriceDesc(
            @Param("region") Region region,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query("""
       SELECT DISTINCT tf.* 
       FROM tour_feature tf
       JOIN tours t ON tf.tour_id = t.tour_id
       JOIN tour_tickets tt ON t.tour_id = tt.tour_id
       WHERE tf.region = :region AND tt.departure_date > CURRENT_DATE
       ORDER BY t.price ASC
       LIMIT :limit OFFSET :offset
       """)
    Flux<TourFeature> findAllByRegionAndDepartureDateAfterOrderByPriceAsc(
            @Param("region") Region region,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    Flux<TourFeature> findTourFeatureByTourId(Long tourId);
}

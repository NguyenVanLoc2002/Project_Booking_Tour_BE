package com.fit.recommendationservice.repositories;

import com.fit.recommendationservice.enums.InteractionType;
import com.fit.recommendationservice.models.CustomerInteraction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface CustomerInteractionRepository  extends ReactiveCrudRepository<CustomerInteraction, String> {
    Flux<CustomerInteraction> findByCusId(Long cusId);
    Flux<CustomerInteraction> findByTourId(Long tourId);

    @Query("INSERT INTO customer_interaction (cus_id, tour_id, interaction_type, interaction_date) " +
            "VALUES (:cusId, :tourId, :interactionType, :interactionDate)")
    Mono<CustomerInteraction> insertCustomerInteraction(
            @Param("cusId") Long cusId,
            @Param("tourId") Long tourId,
            @Param("interactionType") InteractionType interactionType,
            @Param("interactionDate") LocalDate interactionDate);

    @Query(value = "SELECT ci.* FROM customer_interaction ci " +
            "WHERE ci.cus_id = :cusId " +
            "AND ci.interaction_type = :interactionType " +
            "GROUP BY ci.tour_id " +
            "ORDER BY ci.interaction_id")
    Flux<CustomerInteraction> findByCusIdAndInteractionTypeDistinctTourId(
            @Param("cusId") Long cusId,
            @Param("interactionType") String interactionType);

    Mono<Void> deleteByInteractionId(Long interactionId);
}

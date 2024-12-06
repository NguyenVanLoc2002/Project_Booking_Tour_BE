package com.fit.tourservice.repositories.r2dbc;

import com.fit.tourservice.models.ServiceTour;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ServiceTourRepository extends ReactiveCrudRepository<ServiceTour,Long> {

    Flux<ServiceTour> findServiceTourByTourId(Long tourId);
}

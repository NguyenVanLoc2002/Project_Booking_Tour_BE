package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.ServiceTourDTO;
import com.fit.tourservice.services.ServiceTourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/service-tour")
@Slf4j
public class ServiceTourController {
    @Autowired
    private ServiceTourService serviceTourService;

    @GetMapping
    public Mono<ResponseEntity<Flux<ServiceTourDTO>>> getAllServiceTours(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(serviceTourService.getAll(page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching all service tours: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ServiceTourDTO>> addServiceTour(@RequestBody ServiceTourDTO serviceTourDTO) {
        return serviceTourService.addServiceTour(serviceTourDTO)
                .map(savedServiceTour -> ResponseEntity.status(HttpStatus.CREATED).body(savedServiceTour))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{serviceId}")
    public Mono<ResponseEntity<ServiceTourDTO>> updateServiceTour(@PathVariable Long serviceId, @RequestBody ServiceTourDTO serviceTourDTO) {
        return serviceTourService.updateServiceTour(serviceTourDTO, serviceId)
                .map(updatedServiceTour -> ResponseEntity.ok(updatedServiceTour))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{serviceId}")
    public Mono<ResponseEntity<String>> deleteServiceTour(@PathVariable Long serviceId) {
        return serviceTourService.deleteServiceTour(serviceId)
                .then(Mono.just(ResponseEntity.ok("ServiceTour with ID " + serviceId + " has been deleted.")))
                .onErrorResume(error -> {
                    log.error("Error while deleting ServiceTour {}: {}", serviceId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/by-tour")
    public Mono<ResponseEntity<Flux<ServiceTourDTO>>> getServiceToursByTour(@RequestParam Long tourId) {
        return Mono.just(ResponseEntity.ok(serviceTourService.getServiceTourByTourId(tourId)))
                .onErrorResume(e -> {
                    log.error("Error fetching service tours by tour ID: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}


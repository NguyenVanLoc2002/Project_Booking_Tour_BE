package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.ItinerariesDTO;
import com.fit.tourservice.services.ItinerariesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itineraries")
@Slf4j
public class ItinerariesController {
    @Autowired
    private ItinerariesService itinerariesService;



    @GetMapping
    public Mono<ResponseEntity<Flux<ItinerariesDTO>>> getItineraries(@RequestParam int page, @RequestParam int size){
        return Mono.just(ResponseEntity.ok(itinerariesService.getAll(page, size)))
                .onErrorResume(e->{
                    log.error("Error fetching tour notes: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ItinerariesDTO>> addItinerary(@RequestBody ItinerariesDTO itinerariesDTO){
        return itinerariesService.addItineraries(itinerariesDTO)
                .map(itinerariesSaved -> ResponseEntity.status(HttpStatus.CREATED).body(itinerariesSaved))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping("/{itinerariesId}")
    public Mono<ResponseEntity<ItinerariesDTO>> updateItinerary(@PathVariable Long itinerariesId, @RequestBody ItinerariesDTO itinerariesDTO){
        return itinerariesService.updateItineraries(itinerariesDTO, itinerariesId)
                .map(updated-> ResponseEntity.status(HttpStatus.OK).body(updated))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{itinerariesId}")
    public Mono<ResponseEntity<String>> deleteItinerary(@PathVariable Long itinerariesId) {
        return itinerariesService.deleteItineraries(itinerariesId)
                .then(Mono.just(ResponseEntity.ok("Itinerary with ID " + itinerariesId + " has been deleted."))) // Trả về thông báo xác nhận
                .onErrorResume(error -> {
                    log.error("Error while deleting Itinerary {}: {}", itinerariesId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @GetMapping("/by-tour")
    public Mono<ResponseEntity<Flux<ItinerariesDTO>>> getItinerariesByTour(@RequestParam Long tourId) {
        return Mono.just(ResponseEntity.ok(itinerariesService.getItinerariesByTourId(tourId)))
                .onErrorResume(e -> {
                    log.error("Error fetching itineraries by tour ID: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }



}

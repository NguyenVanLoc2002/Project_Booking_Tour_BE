package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.TourNoteDTO;
import com.fit.tourservice.services.TourNoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tour-notes")
@Slf4j
public class TourNoteController {
    @Autowired
    private TourNoteService tourNoteService;

    // Get all tour notes with pagination
    @GetMapping
    public Mono<ResponseEntity<Flux<TourNoteDTO>>> getTourNotes(@RequestParam int page, @RequestParam int size) {
        return Mono.just(ResponseEntity.ok(tourNoteService.getAll(page, size)))
                .onErrorResume(e -> {
                    log.error("Error fetching tour notes: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }


    // Add a new tour note
    @PostMapping("/add")
    public Mono<ResponseEntity<TourNoteDTO>> addTourNote(@RequestBody TourNoteDTO tourNoteDTO) {
        return tourNoteService.addTourNote(tourNoteDTO)
                .map(savedTourNote -> ResponseEntity.status(HttpStatus.CREATED).body(savedTourNote))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    // Update an existing tour note
    @PutMapping("/{noteId}")
    public Mono<ResponseEntity<TourNoteDTO>> updateTourNote(@PathVariable Long noteId, @RequestBody TourNoteDTO tourNoteDTO) {
        return tourNoteService.updateTourNote(tourNoteDTO, noteId)
                .map(updatedNote -> ResponseEntity.status(HttpStatus.OK).body(updatedNote))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Delete a tour note
    @DeleteMapping("/{noteId}")
    public Mono<ResponseEntity<String>> deleteTourNote(@PathVariable Long noteId) {
        return tourNoteService.deleteTourNote(noteId)
                .then(Mono.just(ResponseEntity.ok("Tour Note with ID " + noteId + " has been deleted.")))
                .onErrorResume(error -> {
                    log.error("Error while deleting Tour Note {}: {}", noteId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    // Get tour notes by tour ID
    @GetMapping("/by-tour")
    public Mono<ResponseEntity<Flux<TourNoteDTO>>> getTourNotesByTour(@RequestParam Long tourId) {
        return Mono.just(ResponseEntity.ok(tourNoteService.getTourNotesByTourId(tourId)))
                .onErrorResume(e -> {
                    log.error("Error fetching tour notes by tour ID: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}

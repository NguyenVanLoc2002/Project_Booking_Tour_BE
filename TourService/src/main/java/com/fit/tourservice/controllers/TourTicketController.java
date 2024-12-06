package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.TourTicketDTO;
import com.fit.tourservice.services.TourTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tour-tickets")
public class TourTicketController {

    @Autowired
    private TourTicketService tourTicketService;

    // Lấy tất cả tour tickets với phân trang
    @GetMapping
    public Mono<ResponseEntity<Flux<TourTicketDTO>>> getAllTourTickets(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Flux<TourTicketDTO> tourTickets = tourTicketService.getAllTourTickets(page, size);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(tourTickets)
        );
    }

    // Lấy tour ticket theo ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TourTicketDTO>> getTourTicketById(@PathVariable Long id) {
        return tourTicketService.getTourTicketById(id)
                .map(ticket -> ResponseEntity.ok().body(ticket))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Tạo mới tour ticket
    @PostMapping
    public Mono<ResponseEntity<TourTicketDTO>> createTourTicket(@RequestBody TourTicketDTO tourTicketDTO) {
        return tourTicketService.createTourTicket(tourTicketDTO)
                .map(newTicket -> ResponseEntity.status(HttpStatus.CREATED).body(newTicket));
    }

    // Cập nhật tour ticket theo ID
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TourTicketDTO>> updateTourTicket(
            @PathVariable Long id, @RequestBody TourTicketDTO updatedTicketDTO) {
        return tourTicketService.updateTourTicket(id, updatedTicketDTO)
                .map(updatedTicket -> ResponseEntity.ok().body(updatedTicket))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Xóa tour ticket theo ID
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTourTicket(@PathVariable Long id) {
        return tourTicketService.deleteTourTicket(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    // Lấy tất cả tour tickets theo tourId với ngày khởi hành trong tương lai
    @GetMapping("/by-tour/{tourId}")
    public Mono<ResponseEntity<Flux<TourTicketDTO>>> getFutureTourTicketsByTourId(@PathVariable Long tourId) {
        Flux<TourTicketDTO> tourTickets = tourTicketService.getFutureTourTicketsByTourId(tourId);
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(tourTickets)
        );
    }


    @PostMapping("/refund-slot")
    public Mono<ResponseEntity<TourTicketDTO>> refundSlotTourTicket(
            @RequestParam Long ticketId, @RequestParam int numberSlot) {
        return tourTicketService.refundAvailableSlot(ticketId, numberSlot)
                .map(updatedTicket -> ResponseEntity.ok().body(updatedTicket))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}


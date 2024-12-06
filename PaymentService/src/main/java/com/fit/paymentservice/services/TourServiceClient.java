package com.fit.paymentservice.services;

import com.fit.paymentservice.dtos.TourDTO;
import com.fit.paymentservice.dtos.TourTicketDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TourServiceClient {

    @Autowired
    private WebClient.Builder webClientBuilder;
    private static final String TOUR_TICKET_SERVICE_URL = "http://APIGATEWAY/api/v1/tour-tickets"; // Địa chỉ API của TourService thông qua API GATEWAY
    private static final String TOUR_SERVICE_URL = "http://APIGATEWAY/api/v1/tours";

    public Mono<TourTicketDTO> getTourTicketById(Long ticketId) {
        String url = UriComponentsBuilder.fromHttpUrl(TOUR_TICKET_SERVICE_URL)
                .path("/{id}")
                .buildAndExpand(ticketId)
                .toUriString();

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(TourTicketDTO.class) // Ánh xạ JSON sang TourTicketDTO
                .doOnError(error -> log.error("Error fetching ticket {}: {}", ticketId, error.getMessage()));
    }


    public Mono<TourDTO> getTourByTicketId(Long ticketId) {
        String url = UriComponentsBuilder.fromHttpUrl(TOUR_SERVICE_URL)
                .path("/getById")
                .queryParam("ticketId", ticketId)
                .toUriString();

        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(TourDTO.class) // Ánh xạ JSON sang TourDTO
                .doOnError(error -> log.error("Error fetching tour by ticket ID {}: {}", ticketId, error.getMessage()))
                .onErrorResume(error -> {
                    // Xử lý lỗi, ví dụ trả về một Mono rỗng hoặc thông báo lỗi cụ thể
                    log.error("Fallback for ticket ID {}: {}", ticketId, error.getMessage());
                    return Mono.empty(); // Có thể trả về một fallback Mono nếu cần
                });
    }

    public Mono<TourTicketDTO> refundSlotTourTicket(Long ticketId, int numberSlot) {
        String url = UriComponentsBuilder.fromHttpUrl(TOUR_TICKET_SERVICE_URL)
                .path("/refund-slot")  // Đường dẫn của API refund slot
                .queryParam("ticketId", ticketId)
                .queryParam("numberSlot", numberSlot)
                .toUriString();

        return webClientBuilder.build()
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(TourTicketDTO.class) // Ánh xạ JSON sang TourTicketDTO
                .doOnError(error -> log.error("Error update ticket {}: {}", ticketId, error.getMessage()));
    }

}



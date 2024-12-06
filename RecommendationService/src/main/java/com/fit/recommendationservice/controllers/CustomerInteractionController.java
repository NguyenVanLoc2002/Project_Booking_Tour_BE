package com.fit.recommendationservice.controllers;

import com.fit.recommendationservice.dtos.request.CustomerInteractionRequest;
import com.fit.recommendationservice.dtos.response.CustomerInteractionDTO;
import com.fit.recommendationservice.services.CustomerInteractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/recommendation/customer-interaction")
public class CustomerInteractionController {
    @Autowired
    private CustomerInteractionService customerInteractionService;

    @PostMapping("/interactions")
    public Mono<ResponseEntity<CustomerInteractionDTO>> saveInteraction(
            @RequestBody CustomerInteractionRequest customerInteractionRequest) {
        return customerInteractionService.addCustomerInteraction(customerInteractionRequest)
                .map(savedInteraction -> ResponseEntity
                        .status(HttpStatus.CREATED) // Trạng thái 201 Created
                        .body(savedInteraction))
                .onErrorResume(error -> Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST) // Trạng thái 400 Bad Request nếu lỗi
                        .build()));
    }

    @DeleteMapping("/interactions/{interactionId}")
    public Mono<ResponseEntity<Void>> deleteInteraction(@PathVariable Long interactionId) {
        return customerInteractionService.deleteInteraction(interactionId)
                .then( Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/saved/{cusId}")
    public Mono<ResponseEntity<Flux<CustomerInteractionDTO>>> getSavedInteractionsByCustomer(@PathVariable Long cusId) {
        Flux<CustomerInteractionDTO> interactions = customerInteractionService.getSavedInteractionsByCustomer(cusId);

        return interactions.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return Mono.just(ResponseEntity.ok(interactions)); // Trạng thái 200 OK
                    } else {
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.NOT_FOUND) // Trạng thái 404 nếu không có dữ liệu
                                .build());
                    }
                });
    }

}

package com.fit.recommendationservice.controllers;

import com.fit.recommendationservice.dtos.request.CustomerPreferencesRequest;
import com.fit.recommendationservice.dtos.response.CustomerPreferencesDTO;
import com.fit.recommendationservice.services.CustomerPreferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/recommendation/customer-preference")
public class CustomerPreferenceController {
    @Autowired
    private CustomerPreferenceService customerPreferenceService;

    @PostMapping
    public Mono<ResponseEntity<CustomerPreferencesDTO>> savePreference(
            @RequestBody CustomerPreferencesRequest customerPreferencesRequest) {
        return customerPreferenceService.createCustomerPreference(customerPreferencesRequest)
                .map(savedPreference -> ResponseEntity
                        .status(HttpStatus.CREATED) // Trạng thái 201 Created
                        .body(savedPreference))
                .onErrorResume(error -> Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST) // Trạng thái 400 Bad Request nếu lỗi
                        .build()));
    }
}

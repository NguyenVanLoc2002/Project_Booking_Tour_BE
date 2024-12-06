package com.fit.recommendationservice.services;

import com.fit.recommendationservice.dtos.request.CustomerInteractionRequest;
import com.fit.recommendationservice.dtos.response.CustomerInteractionDTO;
import com.fit.recommendationservice.enums.InteractionType;
import com.fit.recommendationservice.repositories.CustomerInteractionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerInteractionService {
    @Autowired
    private CustomerInteractionRepository customerInteractionRepository;

    public Mono<CustomerInteractionDTO> addCustomerInteraction(CustomerInteractionRequest customerInteractionRequest) {
        return customerInteractionRepository.insertCustomerInteraction(
                customerInteractionRequest.getCusId(),
                customerInteractionRequest.getTourId(),
                customerInteractionRequest.getInteractionType(),
                customerInteractionRequest.getInteractionDate()
        ).map(CustomerInteractionDTO::convertToDTO);
    }

    public Flux<CustomerInteractionDTO> getSavedInteractionsByCustomer(Long cusId) {
        return customerInteractionRepository
                .findByCusIdAndInteractionTypeDistinctTourId(cusId, InteractionType.SAVED.name())
                .map(CustomerInteractionDTO::convertToDTO);
    }

    public Mono<Void> deleteInteraction(Long interactionId){
        return customerInteractionRepository.deleteByInteractionId(interactionId);
    }
}

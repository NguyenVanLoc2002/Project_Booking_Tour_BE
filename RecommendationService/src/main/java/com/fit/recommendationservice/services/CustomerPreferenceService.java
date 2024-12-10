package com.fit.recommendationservice.services;

import com.fit.recommendationservice.dtos.request.CustomerPreferencesRequest;
import com.fit.recommendationservice.dtos.request.TourFilterCriteriaRequest;
import com.fit.recommendationservice.dtos.response.CustomerPreferencesDTO;
import com.fit.recommendationservice.enums.AccommodationQuality;
import com.fit.recommendationservice.enums.Region;
import com.fit.recommendationservice.enums.TransportationMode;
import com.fit.recommendationservice.enums.TypeTour;
import com.fit.recommendationservice.repositories.CustomerPreferencesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Slf4j
public class CustomerPreferenceService {
    @Autowired
    private CustomerPreferencesRepository preferencesRepository;

    public Mono<TourFilterCriteriaRequest> getCommonPreferences(Long customerId) {
        return Mono.zip(
                        preferencesRepository.findLatestPrice(customerId).defaultIfEmpty(0.0),
                        preferencesRepository.findLatestDuration(customerId).defaultIfEmpty(0),
                        preferencesRepository.findPopularDepartureLocation(customerId).defaultIfEmpty(""),
                        preferencesRepository.findPopularTypeTour(customerId).defaultIfEmpty(""),
                        preferencesRepository.findPopularAccommodationQuality(customerId).defaultIfEmpty(""),
                        preferencesRepository.findPopularRegion(customerId).defaultIfEmpty(""),
                        preferencesRepository.findPopularTransportationMode(customerId).defaultIfEmpty("")
                )
                .map(tuple -> new TourFilterCriteriaRequest(
                        tuple.getT1() , // maxCost
                        LocalDate.now(), // startDate
                        tuple.getT2(), // maxDuration
                        !tuple.getT3().isEmpty() ? tuple.getT3() : null,// departureLocation
                        tuple.getT4() != null && !tuple.getT4().isEmpty() ? TypeTour.valueOf(tuple.getT4()) : null, // typeTour
                        tuple.getT5() != null && !tuple.getT5().isEmpty() ? AccommodationQuality.valueOf(tuple.getT5()) : null, // accommodationQuality
                        tuple.getT6() != null && !tuple.getT6().isEmpty() ? Region.valueOf(tuple.getT6()) : null, // region
                        tuple.getT7() != null && !tuple.getT7().isEmpty() ? TransportationMode.valueOf(tuple.getT7()) : null // transportationMode
                ));
    }

    public Mono<CustomerPreferencesDTO> createCustomerPreference(CustomerPreferencesRequest customerPreferencesRequest) {
        return preferencesRepository.insertCustomerPreferences(
                        customerPreferencesRequest.getCusId(),
                        customerPreferencesRequest.getMaxCost(),
                        customerPreferencesRequest.getMaxDuration(),
                        customerPreferencesRequest.getDepartureLocation(),
                        customerPreferencesRequest.getStartDate(),
                        customerPreferencesRequest.getTypeTour(),
                        customerPreferencesRequest.getRegion(),
                        customerPreferencesRequest.getAccommodationQuality(),
                        customerPreferencesRequest.getTransportationMode()
                )
                .map(CustomerPreferencesDTO::convertToDTO);
    }
}

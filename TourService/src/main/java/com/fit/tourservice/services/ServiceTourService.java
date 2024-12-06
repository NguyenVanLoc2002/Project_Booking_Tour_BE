package com.fit.tourservice.services;

import com.fit.tourservice.dtos.ServiceTourDTO;
import com.fit.tourservice.models.ServiceTour;
import com.fit.tourservice.repositories.r2dbc.ServiceTourRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ServiceTourService {
    @Autowired
    private ServiceTourRepository serviceTourRepository;

    public Mono<ServiceTourDTO> addServiceTour(ServiceTourDTO serviceTourDTO) {
        return Mono.just(serviceTourDTO)
                .map(ServiceTourDTO::convertToEntity)
                .flatMap(serviceTour -> serviceTourRepository.save(serviceTour))
                .map(ServiceTourDTO::convertToDTO);
    }

    public Mono<Void> deleteServiceTour(Long serviceId) {
        return serviceTourRepository.findById(serviceId)
                .flatMap(serviceTour -> serviceTourRepository.delete(serviceTour));
    }

    public Mono<ServiceTourDTO> updateServiceTour(ServiceTourDTO serviceTourDTO, Long serviceId) {
        return serviceTourRepository.findById(serviceId)
                .flatMap(serviceTour -> {
                    ServiceTour updatedServiceTour = ServiceTourDTO.convertToEntity(serviceTourDTO);
                    updatedServiceTour.setServiceId(serviceTour.getServiceId());
                    return serviceTourRepository.save(updatedServiceTour);
                })
                .map(ServiceTourDTO::convertToDTO);
    }

    public Flux<ServiceTourDTO> getAll(int page, int size) {
        return serviceTourRepository.findAll()
                .map(ServiceTourDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    public Flux<ServiceTourDTO> getServiceTourByTourId(Long tourId) {
        return serviceTourRepository.findServiceTourByTourId(tourId)
                .map(ServiceTourDTO::convertToDTO);
    }
}


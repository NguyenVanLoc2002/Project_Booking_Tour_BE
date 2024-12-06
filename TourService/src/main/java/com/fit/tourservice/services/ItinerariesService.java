package com.fit.tourservice.services;

import com.fit.tourservice.dtos.ItinerariesDTO;
import com.fit.tourservice.models.Itineraries;
import com.fit.tourservice.repositories.r2dbc.ItinerariesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ItinerariesService {
    @Autowired
    private ItinerariesRepository itinerariesRepository;

    public Mono<ItinerariesDTO> addItineraries(ItinerariesDTO itinerariesDTO) {
        return Mono.just(itinerariesDTO)
                .map(ItinerariesDTO::convertToEntity)
                .flatMap(itineraries -> itinerariesRepository.save(itineraries))
                .map(ItinerariesDTO::convertToDTO);
    }

    public Mono<Void> deleteItineraries(Long itinerariesId) {
        return itinerariesRepository.findById(itinerariesId)
                .flatMap(itineraries -> itinerariesRepository.delete(itineraries));
    }

    public Mono<ItinerariesDTO> updateItineraries(ItinerariesDTO itinerariesDTO, Long itinerariesId) {
        return itinerariesRepository.findById(itinerariesId)
                .flatMap(itineraries -> {
                    Itineraries itinerariesNew = ItinerariesDTO.convertToEntity(itinerariesDTO);
                    itinerariesNew.setItinerId(itineraries.getItinerId());
                    return itinerariesRepository.save(itinerariesNew);
                })
                .map(ItinerariesDTO::convertToDTO);
    }

    //Lay All List Chi tiet tour co phan trang
    public Flux<ItinerariesDTO> getAll(int page, int size){
        return itinerariesRepository.findAll()
                .map(ItinerariesDTO::convertToDTO)
                .skip((long) (page -1) *size)
                .take(size);
    }

    public Flux<ItinerariesDTO> getItinerariesByTourId(Long tourId){
        return itinerariesRepository.findItinerariesByTourId(tourId)
                .map(ItinerariesDTO::convertToDTO);
    }
}

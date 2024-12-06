package com.fit.tourservice.services;

import com.fit.tourservice.dtos.TourFeatureDTO;
import com.fit.tourservice.models.TourFeature;
import com.fit.tourservice.repositories.r2dbc.TourFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TourFeatureService {

    @Autowired
    private TourFeatureRepository tourFeatureRepository;

    public Flux<TourFeatureDTO> getAllTourFeatures(int page, int size) {
        return tourFeatureRepository.findAll()
                .map(TourFeatureDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    public Mono<TourFeatureDTO> getTourFeatureById(Long id) {
        return tourFeatureRepository.findById(id)
                .map(TourFeatureDTO::convertToDTO);
    }

    public Mono<TourFeatureDTO> createTourFeature(TourFeatureDTO tourFeatureDTO) {
        return Mono.just(tourFeatureDTO)
                .map(TourFeatureDTO::convertToEntity)
                .flatMap(tourFeatureRepository::save)
                .map(TourFeatureDTO::convertToDTO);
    }

    public Mono<TourFeatureDTO> updateTourFeature(Long id, TourFeatureDTO updatedFeatureDTO) {
        return tourFeatureRepository.findById(id)
                .flatMap(existingFeature -> {
                    TourFeature updatedFeature = TourFeatureDTO.convertToEntity(updatedFeatureDTO);
                    updatedFeature.setFeatureId(existingFeature.getFeatureId());
                    return tourFeatureRepository.save(updatedFeature);
                })
                .map(TourFeatureDTO::convertToDTO);
    }

    public Mono<Void> deleteTourFeature(Long id) {
        return tourFeatureRepository.deleteById(id);
    }
}

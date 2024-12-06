package com.fit.tourservice.controllers;

import com.fit.tourservice.dtos.TourFeatureDTO;
import com.fit.tourservice.services.TourFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tour-features")
public class TourFeatureController {

    private final TourFeatureService tourFeatureService;

    @Autowired
    public TourFeatureController(TourFeatureService tourFeatureService) {
        this.tourFeatureService = tourFeatureService;
    }

    // Lấy tất cả các TourFeature với pagination
    @GetMapping
    public Flux<TourFeatureDTO> getAllTourFeatures(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return tourFeatureService.getAllTourFeatures(page, size);
    }

    // Lấy một TourFeature theo ID
    @GetMapping("/{id}")
    public Mono<TourFeatureDTO> getTourFeatureById(@PathVariable Long id) {
        return tourFeatureService.getTourFeatureById(id);
    }

    // Tạo mới một TourFeature
    @PostMapping
    public Mono<TourFeatureDTO> createTourFeature(@RequestBody TourFeatureDTO tourFeatureDTO) {
        return tourFeatureService.createTourFeature(tourFeatureDTO);
    }

    // Cập nhật TourFeature theo ID
    @PutMapping("/{id}")
    public Mono<TourFeatureDTO> updateTourFeature(@PathVariable Long id,
                                                  @RequestBody TourFeatureDTO tourFeatureDTO) {
        return tourFeatureService.updateTourFeature(id, tourFeatureDTO);
    }

    // Xóa TourFeature theo ID
    @DeleteMapping("/{id}")
    public Mono<Void> deleteTourFeature(@PathVariable Long id) {
        return tourFeatureService.deleteTourFeature(id);
    }
}

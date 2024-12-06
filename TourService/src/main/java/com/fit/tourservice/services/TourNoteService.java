package com.fit.tourservice.services;

import com.fit.tourservice.dtos.TourNoteDTO;
import com.fit.tourservice.models.TourNote;
import com.fit.tourservice.repositories.r2dbc.TourNoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TourNoteService {
    @Autowired
    private TourNoteRepository tourNoteRepository;

    public Mono<TourNoteDTO> addTourNote(TourNoteDTO tourNoteDTO) {
        return Mono.just(tourNoteDTO)
                .map(TourNoteDTO::convertToEntity)
                .flatMap(tourNote -> tourNoteRepository.save(tourNote))
                .map(TourNoteDTO::convertToDTO);
    }

    public Mono<Void> deleteTourNote(Long noteId) {
        return tourNoteRepository.findById(noteId)
                .flatMap(tourNote -> tourNoteRepository.delete(tourNote));
    }

    public Mono<TourNoteDTO> updateTourNote(TourNoteDTO tourNoteDTO, Long noteId) {
        return tourNoteRepository.findById(noteId)
                .flatMap(tourNote -> {
                    TourNote updatedTourNote = TourNoteDTO.convertToEntity(tourNoteDTO);
                    updatedTourNote.setNoteId(tourNote.getNoteId());
                    return tourNoteRepository.save(updatedTourNote);
                })
                .map(TourNoteDTO::convertToDTO);
    }

    // Get All Tour Notes with pagination
    public Flux<TourNoteDTO> getAll(int page, int size) {
        return tourNoteRepository.findAll()
                .map(TourNoteDTO::convertToDTO)
                .skip((long) (page - 1) * size)
                .take(size);
    }

    // Get Tour Notes by Tour ID
    public Flux<TourNoteDTO> getTourNotesByTourId(Long tourId) {
        return tourNoteRepository.findTourNotesByTourId(tourId)
                .map(TourNoteDTO::convertToDTO);
    }
}

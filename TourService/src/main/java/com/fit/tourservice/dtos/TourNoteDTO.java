package com.fit.tourservice.dtos;

import com.fit.tourservice.models.TourNote;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourNoteDTO {
    private Long noteId;
    private Long tourId;
    private String priceDetailOfChildren;
    private String regulation;
    private String notes;

    public static TourNoteDTO convertToDTO(TourNote tourNote) {
        if (tourNote == null) {
            return null;
        }
        return new TourNoteDTO(
                tourNote.getNoteId(),
                tourNote.getTourId(),
                tourNote.getPriceDetailOfChildren(),
                tourNote.getRegulation(),
                tourNote.getNotes()
        );
    }

    public static TourNote convertToEntity(TourNoteDTO tourNoteDTO) {
        if (tourNoteDTO == null) {
            return null;
        }
        TourNote tourNote = new TourNote();
        tourNote.setNoteId(tourNoteDTO.getNoteId());
        tourNote.setTourId(tourNoteDTO.getTourId());
        tourNote.setPriceDetailOfChildren(tourNoteDTO.getPriceDetailOfChildren());
        tourNote.setRegulation(tourNoteDTO.getRegulation());
        tourNote.setNotes(tourNoteDTO.getNotes());
        return tourNote;
    }
}

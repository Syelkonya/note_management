/*
 * Notes service.
 */
package com.example.notes.dto;

import com.example.notes.model.Note;
import java.util.List;

/**
 * Maps domain notes to transport objects.
 *
 * @since 1.0
 */
public interface NoteMapper {

    /**
     * Map a single note.
     * @param note Domain note
     * @return Transport representation
     */
    NoteResponse toResponse(Note note);

    /**
     * Map a collection of notes.
     * @param notes Domain notes
     * @return Transport representations
     */
    List<NoteResponse> toResponses(List<Note> notes);
}

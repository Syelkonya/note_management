package com.example.notes.dto;

import java.util.List;

import com.example.notes.model.Note;

/**
 * Maps domain {@link Note} objects to transport DTOs.
 */
public final class NoteMapper {

    private NoteMapper() {
    }

    public static NoteResponse toResponse(Note note) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static List<NoteResponse> toResponse(List<Note> notes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

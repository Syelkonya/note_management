package com.example.notes.dto;

import java.util.List;

import com.example.notes.model.Note;

import lombok.experimental.UtilityClass;

/**
 * Maps domain {@link Note} objects to transport DTOs.
 */
@UtilityClass
public class NoteMapper {

    public static NoteResponse toResponse(Note note) {
        return new NoteResponse(
                note.id(),
                note.title(),
                note.content(),
                note.createdAt(),
                note.tags());
    }

    public static List<NoteResponse> toResponse(List<Note> notes) {
        return notes.stream()
                .map(NoteMapper::toResponse)
                .toList();
    }
}

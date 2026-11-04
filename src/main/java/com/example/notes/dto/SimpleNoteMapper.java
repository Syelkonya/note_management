/*
 * Notes service.
 */
package com.example.notes.dto;

import com.example.notes.model.Note;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link NoteMapper}.
 *
 * @since 1.0
 */
@Component
public final class SimpleNoteMapper implements NoteMapper {

    @Override
    public NoteResponse toResponse(final Note note) {
        return new NoteResponse(
            note.id(), note.title(), note.content(),
            note.createdAt(), note.tags()
        );
    }

    @Override
    public List<NoteResponse> toResponses(final List<Note> notes) {
        return notes.stream().map(this::toResponse).toList();
    }
}

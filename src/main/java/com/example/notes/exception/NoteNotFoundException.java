package com.example.notes.exception;

import java.util.UUID;

/**
 * Thrown when a requested note does not exist.
 */
public class NoteNotFoundException extends RuntimeException {

    private final UUID noteId;

    public NoteNotFoundException(UUID noteId) {
        super("Note not found: " + noteId);
        this.noteId = noteId;
    }

    public UUID getNoteId() {
        return noteId;
    }
}

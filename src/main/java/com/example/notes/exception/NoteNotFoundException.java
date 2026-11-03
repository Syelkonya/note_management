package com.example.notes.exception;

import java.util.UUID;

import lombok.Getter;

/**
 * Thrown when a requested note does not exist.
 */
@Getter
public class NoteNotFoundException extends RuntimeException {

    private final UUID noteId;

    public NoteNotFoundException(UUID noteId) {
        super("Note not found: " + noteId);
        this.noteId = noteId;
    }
}

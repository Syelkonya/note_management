/*
 * Notes service.
 */
package com.example.notes.exception;

import java.util.UUID;

/**
 * Thrown when a requested note does not exist.
 *
 * @since 1.0
 */
public final class NoteNotFoundException extends RuntimeException {

    /** Serial version. */
    private static final long serialVersionUID = 1L;

    /** Identifier of the missing note. */
    private final UUID note;

    /**
     * New exception with a generic message.
     * @param note Identifier of the missing note
     */
    public NoteNotFoundException(final UUID note) {
        this(note, "Note not found");
    }

    /**
     * New exception.
     * @param note Identifier of the missing note
     * @param message Error message
     */
    public NoteNotFoundException(final UUID note, final String message) {
        super(message);
        this.note = note;
    }

    /**
     * Identifier of the missing note.
     * @return The identifier
     */
    public UUID identifier() {
        return this.note;
    }
}

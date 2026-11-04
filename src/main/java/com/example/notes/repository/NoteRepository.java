/*
 * Notes service.
 */
package com.example.notes.repository;

import com.example.notes.model.Note;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Storage abstraction for {@link Note} aggregates.
 *
 * @since 1.0
 */
public interface NoteRepository {

    /**
     * Save a note.
     * @param note Note to save
     * @return Saved note
     */
    Note save(Note note);

    /**
     * Find a note by identifier.
     * @param id Identifier
     * @return Found note, if any
     */
    Optional<Note> findById(UUID id);

    /**
     * List all notes.
     * @return All notes
     */
    List<Note> findAll();

    /**
     * List notes having the given tag.
     * @param tag Tag to filter by
     * @return Notes with the tag
     */
    List<Note> findByTag(String tag);

    /**
     * Delete a note by identifier.
     * @param id Identifier
     */
    void deleteById(UUID id);

    /**
     * Check whether a note exists.
     * @param id Identifier
     * @return True if the note exists
     */
    boolean existsById(UUID id);
}

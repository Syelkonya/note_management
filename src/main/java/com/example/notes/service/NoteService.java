/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.dto.NoteRequest;
import com.example.notes.model.Note;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Application service managing notes.
 *
 * @since 1.0
 */
public interface NoteService {

    /**
     * Create a new note.
     * @param title Title of the note
     * @param content Content of the note
     * @param tags Tags of the note
     * @return Created note
     */
    Note create(String title, String content, Set<String> tags);

    /**
     * Find a note by identifier.
     * @param id Identifier
     * @return Found note
     */
    Note get(UUID id);

    /**
     * List all notes.
     * @return All notes
     */
    List<Note> list();

    /**
     * List notes having the given tag.
     * @param tag Tag to filter by
     * @return Notes with the tag
     */
    List<Note> listByTag(String tag);

    /**
     * Update an existing note.
     * @param id Identifier
     * @param request New note data
     * @return Updated note
     */
    Note update(UUID id, NoteRequest request);

    /**
     * Delete a note.
     * @param id Identifier
     */
    void delete(UUID id);
}

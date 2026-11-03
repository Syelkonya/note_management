package com.example.notes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.notes.model.Note;

/**
 * Storage abstraction for {@link Note} aggregates.
 */
public interface NoteRepository {

    Note save(Note note);

    Optional<Note> findById(UUID id);

    List<Note> findAll();

    List<Note> findByTag(String tag);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}

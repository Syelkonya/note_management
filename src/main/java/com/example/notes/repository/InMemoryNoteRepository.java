package com.example.notes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.notes.model.Note;

/**
 * Contract skeleton for the in-memory {@link NoteRepository} implementation.
 * The implementation will be delivered by the implementer through a TDD cycle.
 */
@Repository
public class InMemoryNoteRepository implements NoteRepository {

    @Override
    public Note save(Note note) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<Note> findById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Note> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Note> findByTag(String tag) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean existsById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

package com.example.notes.service;

import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

/**
 * Contract skeleton for {@link NoteService}. Depends only on the
 * {@link NoteRepository} interface (constructor injection).
 */
@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;
    private final Clock clock;

    public NoteServiceImpl(NoteRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Note create(String title, String content, Set<String> tags) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Note get(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Note> list() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Note> listByTag(String tag) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Note update(UUID id, String title, String content, Set<String> tags) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

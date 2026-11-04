/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.dto.NoteRequest;
import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link NoteService}.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public final class NoteServiceImpl implements NoteService {

    /** Note storage. */
    private final NoteRepository repository;

    /** Clock for server-owned timestamps. */
    private final Clock clock;

    @Override
    public Note create(final String title, final String content, final Set<String> tags) {
        return this.repository.save(
            new Note(UUID.randomUUID(), title, content, this.clock.instant(), tags)
        );
    }

    @Override
    public Note get(final UUID id) {
        return this.repository.findById(id).orElseThrow(
            () -> new NoteNotFoundException(
                id, String.format("Note not found: %s", id)
            )
        );
    }

    @Override
    public List<Note> list() {
        return this.repository.findAll();
    }

    @Override
    public List<Note> listByTag(final String tag) {
        final List<Note> result;
        if (tag == null || tag.isBlank()) {
            result = this.repository.findAll();
        } else {
            result = this.repository.findByTag(tag.trim().toLowerCase(Locale.ROOT));
        }
        return result;
    }

    @Override
    public Note update(final UUID id, final NoteRequest request) {
        final Note existing = this.get(id);
        return this.repository.save(
            new Note(
                existing.id(), request.title(), request.content(),
                existing.createdAt(), request.tags()
            )
        );
    }

    @Override
    public void delete(final UUID id) {
        if (!this.repository.existsById(id)) {
            throw new NoteNotFoundException(
                id, String.format("Note not found: %s", id)
            );
        }
        this.repository.deleteById(id);
    }
}

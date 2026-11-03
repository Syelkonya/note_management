package com.example.notes.service;

import java.time.Clock;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Default {@link NoteService}. Depends only on the {@link NoteRepository}
 * interface for storage and on a {@link Clock} for server-owned timestamps.
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;
    private final Clock clock;

    @Override
    public Note create(String title, String content, Set<String> tags) {
        Note note = new Note(
                UUID.randomUUID(),
                title,
                content,
                clock.instant(),
                normalizeTags(tags));
        return repository.save(note);
    }

    @Override
    public Note get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public List<Note> list() {
        return repository.findAll();
    }

    @Override
    public List<Note> listByTag(String tag) {
        if (isBlank(tag)) {
            return repository.findAll();
        }
        return repository.findByTag(tag.trim().toLowerCase(Locale.ROOT));
    }

    @Override
    public Note update(UUID id, String title, String content, Set<String> tags) {
        Note existing = get(id);
        Note updated = new Note(
                existing.id(),
                title,
                content,
                existing.createdAt(),
                normalizeTags(tags));
        return repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NoteNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Trims tags, drops blank entries, lower-cases the rest and removes
     * duplicates while keeping the original iteration order. A {@code null}
     * set is treated as "no tags".
     */
    private static Set<String> normalizeTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return Set.of();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            if (isBlank(tag)) {
                continue;
            }
            normalized.add(tag.trim().toLowerCase(Locale.ROOT));
        }
        return normalized;
    }
}

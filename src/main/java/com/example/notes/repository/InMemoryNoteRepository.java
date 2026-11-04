/*
 * Notes service.
 */
package com.example.notes.repository;

import com.example.notes.model.Note;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Thread-safe in-memory implementation of {@link NoteRepository}.
 *
 * @since 1.0
 */
@Repository
public final class InMemoryNoteRepository implements NoteRepository {

    /** Notes by identifier. */
    private final Map<UUID, Note> notes = new ConcurrentHashMap<>();

    @Override
    public Note save(final Note note) {
        this.notes.put(note.id(), note);
        return note;
    }

    @Override
    public Optional<Note> findById(final UUID id) {
        return Optional.ofNullable(this.notes.get(id));
    }

    @Override
    public List<Note> findAll() {
        return List.copyOf(this.notes.values());
    }

    @Override
    public List<Note> findByTag(final String tag) {
        final List<Note> result;
        final String normalized = InMemoryNoteRepository.normalized(tag);
        if (normalized.isEmpty()) {
            result = List.of();
        } else {
            result = InMemoryNoteRepository.matching(this.notes, normalized);
        }
        return result;
    }

    @Override
    public void deleteById(final UUID id) {
        this.notes.remove(id);
    }

    @Override
    public boolean existsById(final UUID id) {
        return this.notes.containsKey(id);
    }

    /**
     * Select the notes having the given normalized tag.
     * @param notes All notes
     * @param tag Normalized tag
     * @return Matching notes
     */
    private static List<Note> matching(final Map<UUID, Note> notes, final String tag) {
        final List<Note> result = new ArrayList<>(notes.size());
        for (final Note note : notes.values()) {
            if (InMemoryNoteRepository.hasTag(note, tag)) {
                result.add(note);
            }
        }
        return result;
    }

    /**
     * Check whether the note has the given normalized tag.
     * @param note Note to inspect
     * @param tag Normalized tag
     * @return True if the note has the tag
     */
    private static boolean hasTag(final Note note, final String tag) {
        boolean found = false;
        for (final String item : note.tags()) {
            if (InMemoryNoteRepository.normalized(item).equals(tag)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Normalize the tag the same way the domain model does.
     * @param tag Raw tag
     * @return Normalized tag
     */
    private static String normalized(final String tag) {
        final String result;
        if (tag == null) {
            result = "";
        } else {
            result = tag.trim().toLowerCase(Locale.ROOT);
        }
        return result;
    }
}

package com.example.notes.repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.notes.model.Note;

/**
 * Thread-safe, in-memory {@link NoteRepository}. {@link Note} is immutable, so
 * mapping values can be handed out directly; bulk reads return a snapshot.
 */
@Repository
public class InMemoryNoteRepository implements NoteRepository {

    private final Map<UUID, Note> notes = new ConcurrentHashMap<>();

    @Override
    public Note save(Note note) {
        notes.put(note.id(), note);
        return note;
    }

    @Override
    public Optional<Note> findById(UUID id) {
        return Optional.ofNullable(notes.get(id));
    }

    @Override
    public List<Note> findAll() {
        return List.copyOf(notes.values());
    }

    @Override
    public List<Note> findByTag(String tag) {
        String normalized = normalize(tag);
        if (normalized.isEmpty()) {
            return List.of();
        }
        return notes.values().stream()
                .filter(note -> note.tags().stream()
                        .anyMatch(noteTag -> normalize(noteTag).equals(normalized)))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        notes.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return notes.containsKey(id);
    }

    private static String normalize(String tag) {
        return tag == null ? "" : tag.trim().toLowerCase(Locale.ROOT);
    }
}

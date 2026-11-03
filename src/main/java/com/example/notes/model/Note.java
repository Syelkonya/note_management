package com.example.notes.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Domain note. The {@code id}, {@code createdAt} and normalized {@code tags}
 * are owned by the server; clients cannot set them directly.
 */
public record Note(
        UUID id,
        String title,
        String content,
        Instant createdAt,
        Set<String> tags) {

    public Note {
        tags = tags == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    /**
     * Returns an unmodifiable view of the tag set, never the mutable internal
     * representation, so callers cannot change the note.
     */
    @Override
    public Set<String> tags() {
        return Collections.unmodifiableSet(tags);
    }
}

package com.example.notes.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Representation of a note returned to clients.
 */
public record NoteResponse(
        UUID id,
        String title,
        String content,
        Instant createdAt,
        Set<String> tags) {

    public NoteResponse {
        tags = tags == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    /**
     * Returns an unmodifiable view of the tag set, never the mutable internal
     * representation, so callers cannot change the response.
     */
    @Override
    public Set<String> tags() {
        return Collections.unmodifiableSet(tags);
    }
}

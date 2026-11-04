/*
 * Notes service.
 */
package com.example.notes.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Representation of a note returned to clients.
 *
 * @param id Identifier
 * @param title Title of the note
 * @param content Content of the note
 * @param createdAt Creation timestamp
 * @param tags Normalized tags
 * @since 1.0
 */
public record NoteResponse(
    UUID id,
    String title,
    String content,
    Instant createdAt,
    Set<String> tags
) {
    /**
     * Canonical constructor that copies the tags defensively.
     */
    public NoteResponse {
        tags = NoteResponse.copied(tags);
    }

    @Override
    public Set<String> tags() {
        return Collections.unmodifiableSet(this.tags);
    }

    /**
     * Copy the tags into an unmodifiable set.
     * @param tags Raw tags
     * @return Unmodifiable copy
     */
    private static Set<String> copied(final Set<String> tags) {
        final Set<String> result;
        if (tags == null) {
            result = Set.of();
        } else {
            result = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
        }
        return result;
    }
}

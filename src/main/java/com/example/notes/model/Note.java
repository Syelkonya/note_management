/*
 * Notes service.
 */
package com.example.notes.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Domain note.
 *
 * <p>The identifier, the creation time and the normalized tags are owned
 * by the server; clients cannot set them directly.</p>
 *
 * @param id Identifier
 * @param title Title of the note
 * @param content Content of the note
 * @param createdAt Creation timestamp
 * @param tags Normalized tags
 * @since 1.0
 */
public record Note(
    UUID id,
    String title,
    String content,
    Instant createdAt,
    Set<String> tags
) {
    /**
     * Canonical constructor that normalizes the tags.
     */
    public Note {
        tags = Note.normalized(tags);
    }

    @Override
    public Set<String> tags() {
        return Collections.unmodifiableSet(this.tags);
    }

    /**
     * Normalize the tags: trim, lower-case and drop blanks and duplicates.
     * @param tags Raw tags
     * @return Unmodifiable normalized set
     */
    private static Set<String> normalized(final Set<String> tags) {
        final Set<String> result;
        if (tags == null || tags.isEmpty()) {
            result = Set.of();
        } else {
            final Set<String> copy = new LinkedHashSet<>(tags.size());
            for (final String tag : tags) {
                if (tag != null && !tag.isBlank()) {
                    copy.add(tag.trim().toLowerCase(Locale.ROOT));
                }
            }
            result = Collections.unmodifiableSet(copy);
        }
        return result;
    }
}

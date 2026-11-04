/*
 * Notes service.
 */
package com.example.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Payload for creating and updating a note.
 *
 * <p>Server-owned fields, the identifier and the creation time, are
 * deliberately absent.</p>
 *
 * @param title Title of the note
 * @param content Content of the note
 * @param tags Tags of the note
 * @since 1.0
 */
public record NoteRequest(
    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must be at most 255 characters")
    String title,
    @Size(max = 10_000, message = "content must be at most 10000 characters")
    String content,
    Set<String> tags
) {
    /**
     * Canonical constructor that copies the tags defensively.
     */
    public NoteRequest {
        tags = NoteRequest.copied(tags);
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

package com.example.notes.dto;

import java.time.Instant;
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
}

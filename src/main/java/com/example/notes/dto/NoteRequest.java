package com.example.notes.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload for creating and updating a note. Server-owned fields
 * ({@code id}, {@code createdAt}) are deliberately absent.
 */
public record NoteRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Size(max = 10000, message = "content must be at most 10000 characters")
        String content,

        Set<String> tags) {
}

package com.example.notes.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.notes.model.Note;

/**
 * Unit tests for {@link NoteMapper}: every domain field must reach the
 * transport DTO unchanged, including the unmodifiable tag set.
 */
@DisplayName("NoteMapper")
class NoteMapperTest {

    private static final UUID FIRST_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID SECOND_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    private static Note note(UUID id, String title, String content, String... tags) {
        return new Note(id, title, content, CREATED_AT, Set.of(tags));
    }

    @Test
    @DisplayName("toResponse maps every field and keeps the unmodifiable tag set")
    void toResponseMapsEveryField() {
        Note note = note(FIRST_ID, "Title", "Content", "work", "home");

        NoteResponse response = NoteMapper.toResponse(note);

        assertThat(response.id()).isEqualTo(FIRST_ID);
        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.content()).isEqualTo("Content");
        assertThat(response.createdAt()).isEqualTo(CREATED_AT);
        assertThat(response.tags()).containsExactlyInAnyOrder("work", "home");
        Set<String> tags = response.tags();
        assertThatThrownBy(() -> tags.add("other"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("toResponse maps a list preserving the repository order")
    void toResponseMapsListPreservingOrder() {
        Note first = note(FIRST_ID, "First", "First content", "work");
        Note second = note(SECOND_ID, "Second", "Second content", "home");

        List<NoteResponse> responses = NoteMapper.toResponse(List.of(first, second));

        assertThat(responses)
                .extracting(NoteResponse::id)
                .containsExactly(FIRST_ID, SECOND_ID);
    }
}

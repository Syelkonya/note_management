package com.example.notes.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Note} record. The canonical constructor owns the
 * tag-set contract: nulls become an empty set, duplicates are collapsed by the
 * {@link LinkedHashSet} copy and callers get an unmodifiable view.
 */
@DisplayName("Note")
class NoteTest {

    private static final UUID NOTE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    @Test
    @DisplayName("null tags are replaced with an empty set")
    void nullTagsBecomeEmptySet() {
        Note note = new Note(NOTE_ID, "Title", "Content", CREATED_AT, null);

        assertThat(note.tags()).isEmpty();
    }

    @Test
    @DisplayName("tags are defensively copied, de-duplicated and unmodifiable")
    void tagsAreDefensivelyCopiedAndUnmodifiable() {
        Set<String> source = new LinkedHashSet<>(List.of("work", "personal", "work"));

        Note note = new Note(NOTE_ID, "Title", "Content", CREATED_AT, source);
        source.add("later");

        assertThat(note.tags()).containsExactly("work", "personal");
        assertThatThrownBy(() -> note.tags().add("other"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}

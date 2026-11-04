/*
 * Notes service.
 */
package com.example.notes.model;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Note}.
 *
 * @since 1.0
 */
final class NoteTest {

    /** Note identifier. */
    private static final UUID NOTE_ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample tag. */
    private static final String TAG = "work";

    /** Note under test. */
    private Note note;

    @BeforeEach
    void prepareNote() {
        this.note = new Note(
            NoteTest.NOTE_ID, "Title", "Content", NoteTest.CREATED,
            Set.of(NoteTest.TAG)
        );
    }

    @Test
    void replacesNullTagsWithEmptySet() {
        Assertions.assertThat(
            new Note(
                NoteTest.NOTE_ID, "Title", "Content", NoteTest.CREATED, null
            ).tags()
        ).isEmpty();
    }

    @Test
    void copiesTagsDefensively() {
        final Set<String> source = new LinkedHashSet<>(
            List.of(NoteTest.TAG, "personal")
        );
        this.note = new Note(
            NoteTest.NOTE_ID, "Title", "Content", NoteTest.CREATED, source
        );
        source.add("later");
        Assertions.assertThat(this.note.tags()).containsExactly(
            NoteTest.TAG, "personal"
        );
    }

    @Test
    void makesTagsUnmodifiable() {
        Assertions.assertThat(
            this.note.tags()
        ).isUnmodifiable();
    }

    @Test
    void normalizesTags() {
        Assertions.assertThat(
            new Note(
                NoteTest.NOTE_ID, "Title", "Content", NoteTest.CREATED,
                Set.of("  Work ", "personal", "")
            ).tags()
        ).containsExactlyInAnyOrder(
            NoteTest.TAG, "personal"
        );
    }
}

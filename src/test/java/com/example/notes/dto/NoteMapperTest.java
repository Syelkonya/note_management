/*
 * Notes service.
 */
package com.example.notes.dto;

import com.example.notes.model.Note;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SimpleNoteMapper}.
 *
 * @since 1.0
 */
final class NoteMapperTest {

    /** First note identifier. */
    private static final UUID FIRST =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Second note identifier. */
    private static final UUID SECOND =
        UUID.fromString("11111111-2222-3333-4444-555555555555");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample note. */
    private static final Note NOTE = new Note(
        NoteMapperTest.FIRST, "Title", "Body", NoteMapperTest.CREATED,
        Set.of("work")
    );

    /** Mapper under test. */
    private final NoteMapper mapper = new SimpleNoteMapper();

    @Test
    void mapsIdentifier() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).id()
        ).isEqualTo(
            NoteMapperTest.FIRST
        );
    }

    @Test
    void mapsTitle() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).title()
        ).isEqualTo(
            "Title"
        );
    }

    @Test
    void mapsContent() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).content()
        ).isEqualTo(
            "Body"
        );
    }

    @Test
    void mapsCreationTime() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).createdAt()
        ).isEqualTo(
            NoteMapperTest.CREATED
        );
    }

    @Test
    void mapsTags() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).tags()
        ).containsExactly(
            "work"
        );
    }

    @Test
    void makesTagsUnmodifiable() {
        Assertions.assertThat(
            this.mapper.toResponse(NoteMapperTest.NOTE).tags()
        ).isUnmodifiable();
    }

    @Test
    void mapsCollection() {
        Assertions.assertThat(
            this.mapper.toResponses(
                List.of(
                    NoteMapperTest.NOTE,
                    NoteMapperTest.another()
                )
            )
        ).hasSize(
            2
        );
    }

    /**
     * Another sample note.
     * @return Sample note
     */
    private static Note another() {
        return new Note(
            NoteMapperTest.SECOND, "Second", "Body", NoteMapperTest.CREATED,
            Set.of("home")
        );
    }
}

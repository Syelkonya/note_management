/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for note reading in {@link NoteServiceImpl}.
 *
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
final class NoteServiceImplReadTest {

    /** Known note identifier. */
    private static final UUID NOTE_ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Unknown note identifier. */
    private static final UUID UNKNOWN =
        UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    /** Current timestamp. */
    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    /** Original timestamp. */
    private static final Instant OLD = Instant.parse("2023-05-01T10:15:30Z");

    /** Sample tags. */
    private static final Set<String> TAGS = Set.of("work");

    /** Repository mock. */
    @Mock
    private NoteRepository repository;

    /** Service under test. */
    private NoteServiceImpl service;

    @BeforeEach
    void prepareService() {
        this.service = new NoteServiceImpl(
            this.repository, Clock.fixed(NoteServiceImplReadTest.NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void getReturnsNote() {
        Mockito.when(
            this.repository.findById(NoteServiceImplReadTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplReadTest.note())
        );
        Assertions.assertThat(
            this.service.get(NoteServiceImplReadTest.NOTE_ID).title()
        ).isEqualTo(
            "Title"
        );
    }

    @Test
    void getUnknownThrows() {
        Mockito.when(
            this.repository.findById(NoteServiceImplReadTest.UNKNOWN)
        ).thenReturn(
            Optional.empty()
        );
        Assertions.assertThatThrownBy(
            () -> this.service.get(NoteServiceImplReadTest.UNKNOWN)
        ).isInstanceOf(
            NoteNotFoundException.class
        );
    }

    @Test
    void listUsesRepository() {
        Mockito.when(
            this.repository.findAll()
        ).thenReturn(
            List.of(NoteServiceImplReadTest.note())
        );
        Assertions.assertThat(
            this.service.list()
        ).containsExactly(
            NoteServiceImplReadTest.note()
        );
    }

    @Test
    void listByTagNormalizes() {
        Mockito.when(
            this.repository.findByTag("work")
        ).thenReturn(
            List.of(NoteServiceImplReadTest.note())
        );
        Assertions.assertThat(
            this.service.listByTag(" WORK ")
        ).containsExactly(
            NoteServiceImplReadTest.note()
        );
    }

    @Test
    void listByTagWithNullListsAll() {
        Mockito.when(
            this.repository.findAll()
        ).thenReturn(
            List.of(NoteServiceImplReadTest.note())
        );
        Assertions.assertThat(
            this.service.listByTag(null)
        ).containsExactly(
            NoteServiceImplReadTest.note()
        );
    }

    @Test
    void listByTagWithBlankListsAll() {
        Mockito.when(
            this.repository.findAll()
        ).thenReturn(
            List.of(NoteServiceImplReadTest.note())
        );
        Assertions.assertThat(
            this.service.listByTag("   ")
        ).containsExactly(
            NoteServiceImplReadTest.note()
        );
    }

    /**
     * Sample note.
     * @return Sample note
     */
    private static Note note() {
        return new Note(
            NoteServiceImplReadTest.NOTE_ID, "Title", "Body",
            NoteServiceImplReadTest.OLD, NoteServiceImplReadTest.TAGS
        );
    }
}

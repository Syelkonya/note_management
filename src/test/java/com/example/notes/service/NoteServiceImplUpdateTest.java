/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.dto.NoteRequest;
import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
 * Tests for note update in {@link NoteServiceImpl}.
 *
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
final class NoteServiceImplUpdateTest {

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

    /** Update request. */
    private static final NoteRequest REQUEST = new NoteRequest(
        "New title", "New content", NoteServiceImplUpdateTest.TAGS
    );

    /** Request with raw tags. */
    private static final NoteRequest RAW = new NoteRequest(
        "Raw title", "Raw content", Set.of(" Work ")
    );

    /** Request with null tags. */
    private static final NoteRequest EMPTY = new NoteRequest(
        "Null title", "Null content", null
    );

    /** Repository mock. */
    @Mock
    private NoteRepository repository;

    /** Service under test. */
    private NoteServiceImpl service;

    @BeforeEach
    void prepareService() {
        this.service = new NoteServiceImpl(
            this.repository, Clock.fixed(NoteServiceImplUpdateTest.NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void updateKeepsCreationTime() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.REQUEST
            ).createdAt()
        ).isEqualTo(
            NoteServiceImplUpdateTest.OLD
        );
    }

    @Test
    void updateKeepsIdentifier() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.REQUEST
            ).id()
        ).isEqualTo(
            NoteServiceImplUpdateTest.NOTE_ID
        );
    }

    @Test
    void updateReplacesTitle() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.REQUEST
            ).title()
        ).isEqualTo(
            "New title"
        );
    }

    @Test
    void updateReplacesContent() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.REQUEST
            ).content()
        ).isEqualTo(
            "New content"
        );
    }

    @Test
    void updateNormalizesTags() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.RAW
            ).tags()
        ).containsExactly(
            "work"
        );
    }

    @Test
    void updateWithNullTagsStoresEmptySet() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.NOTE_ID)
        ).thenReturn(
            Optional.of(NoteServiceImplUpdateTest.note())
        );
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.service.update(
                NoteServiceImplUpdateTest.NOTE_ID,
                NoteServiceImplUpdateTest.EMPTY
            ).tags()
        ).isEmpty();
    }

    @Test
    void updateUnknownThrows() {
        Mockito.when(
            this.repository.findById(NoteServiceImplUpdateTest.UNKNOWN)
        ).thenReturn(
            Optional.empty()
        );
        Assertions.assertThatThrownBy(
            () -> this.service.update(
                NoteServiceImplUpdateTest.UNKNOWN,
                NoteServiceImplUpdateTest.REQUEST
            )
        ).isInstanceOfSatisfying(
            NoteNotFoundException.class,
            error -> Mockito.verify(
                this.repository, Mockito.never()
            ).save(
                Mockito.any(Note.class)
            )
        );
    }

    /**
     * Sample note.
     * @return Sample note
     */
    private static Note note() {
        return new Note(
            NoteServiceImplUpdateTest.NOTE_ID, "Title", "Body",
            NoteServiceImplUpdateTest.OLD, NoteServiceImplUpdateTest.TAGS
        );
    }
}

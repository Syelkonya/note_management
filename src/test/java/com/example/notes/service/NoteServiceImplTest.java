/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for note creation in {@link NoteServiceImpl}.
 *
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
final class NoteServiceImplTest {

    /** Current timestamp. */
    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

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
            this.repository, Clock.fixed(NoteServiceImplTest.NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void createUsesClock() {
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.created(NoteServiceImplTest.TAGS).createdAt()
        ).isEqualTo(
            NoteServiceImplTest.NOW
        );
    }

    @Test
    void createAssignsIdentifier() {
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.created(NoteServiceImplTest.TAGS).id()
        ).isNotNull();
    }

    @Test
    void createNormalizesTags() {
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.created(Set.of("  Work ", "work")).tags()
        ).containsExactly(
            "work"
        );
    }

    @Test
    void createHandlesNullTags() {
        Mockito.when(
            this.repository.save(Mockito.any(Note.class))
        ).thenAnswer(
            item -> item.getArgument(0)
        );
        Assertions.assertThat(
            this.created(null).tags()
        ).isEmpty();
    }

    /**
     * Create a note with the given tags.
     * @param tags Tags of the note
     * @return Created note
     */
    private Note created(final Set<String> tags) {
        return this.service.create("My title", "Body", tags);
    }
}

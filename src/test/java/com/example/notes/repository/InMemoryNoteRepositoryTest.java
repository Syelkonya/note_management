/*
 * Notes service.
 */
package com.example.notes.repository;

import com.example.notes.model.Note;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for note reading in {@link InMemoryNoteRepository}.
 *
 * @since 1.0
 */
final class InMemoryNoteRepositoryTest {

    /** First note identifier. */
    private static final UUID FIRST =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

    /** Second note identifier. */
    private static final UUID SECOND =
        UUID.fromString("22222222-2222-2222-2222-222222222222");

    /** Unknown note identifier. */
    private static final UUID UNKNOWN =
        UUID.fromString("99999999-9999-9999-9999-999999999999");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Repository under test. */
    private final InMemoryNoteRepository repository = new InMemoryNoteRepository();

    @Test
    void findByIdReturnsEmptyForUnknownNote() {
        Assertions.assertThat(
            this.repository.findById(InMemoryNoteRepositoryTest.UNKNOWN)
        ).isEmpty();
    }

    @Test
    void findAllReturnsEverySavedNote() {
        this.repository.save(
            InMemoryNoteRepositoryTest.note(InMemoryNoteRepositoryTest.FIRST, "First")
        );
        this.repository.save(
            InMemoryNoteRepositoryTest.note(InMemoryNoteRepositoryTest.SECOND, "Second")
        );
        Assertions.assertThat(
            this.repository.findAll()
        ).containsExactlyInAnyOrder(
            InMemoryNoteRepositoryTest.note(InMemoryNoteRepositoryTest.FIRST, "First"),
            InMemoryNoteRepositoryTest.note(InMemoryNoteRepositoryTest.SECOND, "Second")
        );
    }

    @Test
    void findAllReturnsEmptyInitially() {
        Assertions.assertThat(
            this.repository.findAll()
        ).isEmpty();
    }

    @Test
    void findByTagIgnoresCaseAndTrimsQuery() {
        this.repository.save(
            InMemoryNoteRepositoryTest.tagged(InMemoryNoteRepositoryTest.FIRST, "work")
        );
        this.repository.save(
            InMemoryNoteRepositoryTest.tagged(InMemoryNoteRepositoryTest.SECOND, "Work")
        );
        Assertions.assertThat(
            this.repository.findByTag(" WORK ")
        ).containsExactlyInAnyOrder(
            InMemoryNoteRepositoryTest.tagged(InMemoryNoteRepositoryTest.FIRST, "work"),
            InMemoryNoteRepositoryTest.tagged(InMemoryNoteRepositoryTest.SECOND, "Work")
        );
    }

    @Test
    void findByTagReturnsEmptyWithoutMatches() {
        this.repository.save(
            InMemoryNoteRepositoryTest.tagged(InMemoryNoteRepositoryTest.FIRST, "work")
        );
        Assertions.assertThat(
            this.repository.findByTag("home")
        ).isEmpty();
    }

    @Test
    void findByTagTreatsNullQueryAsEmpty() {
        Assertions.assertThat(
            this.repository.findByTag(null)
        ).isEmpty();
    }

    @Test
    void findByTagTreatsBlankQueryAsEmpty() {
        Assertions.assertThat(
            this.repository.findByTag("   ")
        ).isEmpty();
    }

    @Test
    void findAllReturnsUnmodifiableSnapshot() {
        Assertions.assertThat(
            this.repository.findAll()
        ).isUnmodifiable();
    }

    /**
     * Sample note without tags.
     * @param id Identifier
     * @param title Title
     * @return Sample note
     */
    private static Note note(final UUID id, final String title) {
        return new Note(
            id, title, "Body", InMemoryNoteRepositoryTest.CREATED, Set.of()
        );
    }

    /**
     * Sample note with a tag.
     * @param id Identifier
     * @param tag Tag
     * @return Sample note
     */
    private static Note tagged(final UUID id, final String tag) {
        return new Note(
            id, "Title", "Body", InMemoryNoteRepositoryTest.CREATED, Set.of(tag)
        );
    }
}

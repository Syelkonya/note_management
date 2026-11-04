/*
 * Notes service.
 */
package com.example.notes.repository;

import com.example.notes.model.Note;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for note writing in {@link InMemoryNoteRepository}.
 *
 * @since 1.0
 */
final class InMemoryNoteRepositoryWriteTest {

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

    /** Snapshot captured by a test. */
    private List<Note> snapshot = List.of();

    @Test
    void savesNote() {
        Assertions.assertThat(
            this.repository.save(
                InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
            )
        ).isEqualTo(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
    }

    @Test
    void replacesNoteWithSameIdentifier() {
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        Assertions.assertThat(
            this.repository.findAll()
        ).hasSize(
            1
        );
    }

    @Test
    void deleteByIdRemovesNote() {
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        this.repository.deleteById(InMemoryNoteRepositoryWriteTest.FIRST);
        Assertions.assertThat(
            this.repository.findById(InMemoryNoteRepositoryWriteTest.FIRST)
        ).isEmpty();
    }

    @Test
    void deleteByIdOfUnknownIsSilent() {
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        this.repository.deleteById(InMemoryNoteRepositoryWriteTest.UNKNOWN);
        Assertions.assertThat(
            this.repository.findAll()
        ).hasSize(
            1
        );
    }

    @Test
    void existsByIdReflectsStoredNotes() {
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        Assertions.assertThat(
            this.repository.existsById(InMemoryNoteRepositoryWriteTest.FIRST)
        ).isTrue();
    }

    @Test
    void findAllSnapshotDoesNotChangeAfterSave() {
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.FIRST)
        );
        this.snapshot = this.repository.findAll();
        this.repository.save(
            InMemoryNoteRepositoryWriteTest.note(InMemoryNoteRepositoryWriteTest.SECOND)
        );
        Assertions.assertThat(this.snapshot).hasSize(1);
    }

    @Test
    void snapshotStartsEmpty() {
        Assertions.assertThat(this.snapshot).isEmpty();
    }

    /**
     * Sample note.
     * @param id Identifier
     * @return Sample note
     */
    private static Note note(final UUID id) {
        return new Note(
            id, "First", "Body", InMemoryNoteRepositoryWriteTest.CREATED, Set.of()
        );
    }
}

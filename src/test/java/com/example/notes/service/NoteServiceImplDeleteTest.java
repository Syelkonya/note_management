/*
 * Notes service.
 */
package com.example.notes.service;

import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.repository.NoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for note deletion in {@link NoteServiceImpl}.
 *
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
final class NoteServiceImplDeleteTest {

    /** Known note identifier. */
    private static final UUID NOTE_ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Unknown note identifier. */
    private static final UUID UNKNOWN =
        UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    /** Current timestamp. */
    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    /** Repository mock. */
    @Mock
    private NoteRepository repository;

    /** Service under test. */
    private NoteServiceImpl service;

    @BeforeEach
    void prepareService() {
        this.service = new NoteServiceImpl(
            this.repository, Clock.fixed(NoteServiceImplDeleteTest.NOW, ZoneOffset.UTC)
        );
    }

    @Test
    void deleteRemovesNote() {
        Mockito.when(
            this.repository.existsById(NoteServiceImplDeleteTest.NOTE_ID)
        ).thenReturn(
            true
        );
        this.service.delete(NoteServiceImplDeleteTest.NOTE_ID);
        Mockito.verify(
            this.repository
        ).deleteById(
            NoteServiceImplDeleteTest.NOTE_ID
        );
    }

    @Test
    void deleteUnknownThrows() {
        Mockito.when(
            this.repository.existsById(NoteServiceImplDeleteTest.UNKNOWN)
        ).thenReturn(
            false
        );
        Assertions.assertThatThrownBy(
            () -> this.service.delete(NoteServiceImplDeleteTest.UNKNOWN)
        ).isInstanceOfSatisfying(
            NoteNotFoundException.class,
            error -> Mockito.verify(
                this.repository, Mockito.never()
            ).deleteById(
                Mockito.any()
            )
        );
    }
}

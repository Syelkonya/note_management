package com.example.notes.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.notes.model.Note;

/**
 * Unit tests for the in-memory {@link NoteRepository}. Plain JUnit 5: the
 * repository is instantiated directly, no Spring context is started.
 */
@DisplayName("InMemoryNoteRepository")
class InMemoryNoteRepositoryTest {

    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    private static final UUID NOTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID THIRD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID UNKNOWN_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    private InMemoryNoteRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNoteRepository();
    }

    private static Note note(UUID id, String title, String content, String... tags) {
        return new Note(id, title, content, CREATED_AT, Set.of(tags));
    }

    @Test
    @DisplayName("save stores the note and returns it")
    void saveStoresNoteAndReturnsIt() {
        Note note = note(NOTE_ID, "First", "First content", "work");

        Note saved = repository.save(note);

        assertThat(saved).isEqualTo(note);
        assertThat(repository.findById(NOTE_ID)).contains(note);
    }

    @Test
    @DisplayName("saving a note with an existing id replaces the previous note")
    void saveWithSameIdReplacesExistingNote() {
        Note original = note(NOTE_ID, "Original", "Original content", "work");
        Note replacement = note(NOTE_ID, "Replacement", "Replacement content", "personal");

        repository.save(original);
        Note saved = repository.save(replacement);

        assertThat(saved).isEqualTo(replacement);
        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findById(NOTE_ID)).contains(replacement);
    }

    @Test
    @DisplayName("findById returns Optional.empty() for an unknown id")
    void findByIdForUnknownIdReturnsEmpty() {
        repository.save(note(NOTE_ID, "Known", "Known content"));

        assertThat(repository.findById(UNKNOWN_ID)).isEmpty();
    }

    @Test
    @DisplayName("findAll returns every saved note")
    void findAllReturnsEverySavedNote() {
        Note first = note(NOTE_ID, "First", "First content");
        Note second = note(OTHER_ID, "Second", "Second content");
        Note third = note(THIRD_ID, "Third", "Third content");
        repository.save(first);
        repository.save(second);
        repository.save(third);

        assertThat(repository.findAll()).containsExactlyInAnyOrder(first, second, third);
    }

    @Test
    @DisplayName("findAll returns an empty list when nothing was saved")
    void findAllIsEmptyWhenNothingWasSaved() {
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("findByTag matches case-insensitively and trims the query")
    void findByTagMatchesCaseInsensitivelyAndTrimsQuery() {
        Note lowerCaseTag = note(NOTE_ID, "Work note", "lower case tag", "work", "personal");
        Note upperCaseTag = note(OTHER_ID, "Uppercase tag", "upper case tag", "Work");
        repository.save(lowerCaseTag);
        repository.save(upperCaseTag);

        assertThat(repository.findByTag(" WORK ")).containsExactlyInAnyOrder(lowerCaseTag, upperCaseTag);
    }

    @Test
    @DisplayName("findByTag returns an empty list when no note has the tag")
    void findByTagReturnsEmptyListWhenNoNoteMatches() {
        repository.save(note(NOTE_ID, "Work note", "content", "work"));

        assertThat(repository.findByTag("home")).isEmpty();
    }

    @Test
    @DisplayName("deleteById removes the stored note")
    void deleteByIdRemovesNote() {
        Note note = note(NOTE_ID, "Doomed", "content", "work");
        repository.save(note);

        repository.deleteById(NOTE_ID);

        assertThat(repository.findById(NOTE_ID)).isEmpty();
        assertThat(repository.findAll()).isEmpty();
        assertThat(repository.existsById(NOTE_ID)).isFalse();
    }

    @Test
    @DisplayName("deleteById for an unknown id is a silent no-op")
    void deleteByIdForUnknownIdIsSilentNoOp() {
        Note note = note(NOTE_ID, "Kept", "content", "work");
        repository.save(note);

        assertThatCode(() -> repository.deleteById(UNKNOWN_ID)).doesNotThrowAnyException();

        assertThat(repository.findById(NOTE_ID)).contains(note);
        assertThat(repository.findAll()).containsExactly(note);
    }

    @Test
    @DisplayName("existsById reflects the notes currently stored")
    void existsByIdReflectsStoredNotes() {
        assertThat(repository.existsById(NOTE_ID)).isFalse();

        repository.save(note(NOTE_ID, "Kept", "content"));

        assertThat(repository.existsById(NOTE_ID)).isTrue();
        assertThat(repository.existsById(UNKNOWN_ID)).isFalse();

        repository.deleteById(NOTE_ID);

        assertThat(repository.existsById(NOTE_ID)).isFalse();
    }
}

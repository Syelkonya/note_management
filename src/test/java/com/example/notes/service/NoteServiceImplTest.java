package com.example.notes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.repository.NoteRepository;

/**
 * Unit tests for {@link NoteServiceImpl}. The repository is a Mockito mock and
 * the {@link Clock} is fixed, so every assertion is deterministic.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NoteServiceImpl")
class NoteServiceImplTest {

    private static final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");
    private static final UUID NOTE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID OTHER_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID UNKNOWN_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    @Mock
    private NoteRepository repository;

    private NoteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new NoteServiceImpl(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static Note note(UUID id, String title, String content, Instant createdAt, String... tags) {
        return new Note(id, title, content, createdAt, Set.of(tags));
    }

    @Test
    @DisplayName("create assigns a UUID, uses the clock and normalizes tags before saving")
    void createAssignsIdCreatedAtAndNormalizesTags() {
        when(repository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Set<String> rawTags = new LinkedHashSet<>(
                List.of("  Work ", "work", "", "   ", "PERSONAL", "personal"));

        Note created = service.create("My title", "My content", rawTags);

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(repository).save(captor.capture());
        Note saved = captor.getValue();

        assertThat(created).isEqualTo(saved);
        assertThat(saved.id()).isNotNull();
        assertThat(saved.title()).isEqualTo("My title");
        assertThat(saved.content()).isEqualTo("My content");
        assertThat(saved.createdAt()).isEqualTo(NOW);
        assertThat(saved.tags()).containsExactlyInAnyOrder("work", "personal");
    }

    @Test
    @DisplayName("create treats null tags as an empty set")
    void createWithNullTagsStoresEmptySet() {
        when(repository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note created = service.create("My title", "My content", null);

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().tags()).isEmpty();
        assertThat(created.tags()).isEmpty();
    }

    @Test
    @DisplayName("get returns the note from the repository")
    void getReturnsNoteFromRepository() {
        Note note = note(NOTE_ID, "Title", "Content", NOW, "work");
        when(repository.findById(NOTE_ID)).thenReturn(Optional.of(note));

        assertThat(service.get(NOTE_ID)).isEqualTo(note);
        verify(repository).findById(NOTE_ID);
    }

    @Test
    @DisplayName("get throws NoteNotFoundException for an unknown id")
    void getUnknownIdThrowsNotFound() {
        when(repository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(UNKNOWN_ID))
                .isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    @DisplayName("list delegates to repository.findAll")
    void listDelegatesToRepository() {
        List<Note> notes = List.of(
                note(NOTE_ID, "First", "First content", NOW, "work"),
                note(OTHER_ID, "Second", "Second content", NOW, "home"));
        when(repository.findAll()).thenReturn(notes);

        assertThat(service.list()).containsExactlyElementsOf(notes);
        verify(repository).findAll();
    }

    @Test
    @DisplayName("listByTag normalizes the query and delegates to repository.findByTag")
    void listByTagNormalizesQueryAndDelegates() {
        List<Note> notes = List.of(note(NOTE_ID, "Work note", "Content", NOW, "work"));
        when(repository.findByTag("work")).thenReturn(notes);

        assertThat(service.listByTag(" WORK ")).containsExactlyElementsOf(notes);
        verify(repository).findByTag("work");
    }

    @Test
    @DisplayName("update keeps id and createdAt, replaces fields and saves")
    void updatePreservesIdAndCreatedAtAndReplacesFields() {
        Instant originalCreatedAt = Instant.parse("2023-05-01T10:15:30Z");
        Note existing = note(NOTE_ID, "Old title", "Old content", originalCreatedAt, "old");
        when(repository.findById(NOTE_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note updated = service.update(NOTE_ID, "New title", "New content", Set.of("work"));

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(repository).save(captor.capture());
        Note saved = captor.getValue();

        assertThat(updated).isEqualTo(saved);
        assertThat(saved.id()).isEqualTo(NOTE_ID);
        assertThat(saved.createdAt()).isEqualTo(originalCreatedAt);
        assertThat(saved.title()).isEqualTo("New title");
        assertThat(saved.content()).isEqualTo("New content");
        assertThat(saved.tags()).containsExactly("work");
    }

    @Test
    @DisplayName("update throws NoteNotFoundException and never saves for an unknown id")
    void updateUnknownIdThrowsNotFoundAndDoesNotSave() {
        when(repository.findById(UNKNOWN_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(UNKNOWN_ID, "New title", "New content", Set.of("work")))
                .isInstanceOf(NoteNotFoundException.class);

        verify(repository, never()).save(any(Note.class));
    }

    @Test
    @DisplayName("delete throws NoteNotFoundException and never deletes for an unknown id")
    void deleteUnknownIdThrowsNotFoundAndDoesNotDelete() {
        when(repository.existsById(UNKNOWN_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(UNKNOWN_ID))
                .isInstanceOf(NoteNotFoundException.class);

        verify(repository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("delete removes an existing note through the repository")
    void deleteExistingNoteDelegatesToRepository() {
        when(repository.existsById(NOTE_ID)).thenReturn(true);

        service.delete(NOTE_ID);

        verify(repository).deleteById(NOTE_ID);
    }
}

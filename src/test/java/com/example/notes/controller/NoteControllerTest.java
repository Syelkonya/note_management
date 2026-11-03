package com.example.notes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.service.NoteService;

/**
 * Web-layer tests for {@link NoteController}. All collaborators are mocked:
 * only HTTP status codes, headers and JSON fields that are part of the DTO
 * contract are asserted (error response bodies are implementer-owned).
 */
@WebMvcTest(NoteController.class)
@DisplayName("NoteController")
class NoteControllerTest {

    private static final UUID NOTE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    private static final String VALID_BODY = """
            {"title":"My note","content":"Body","tags":["work"]}""";
    private static final String BLANK_TITLE_BODY = """
            {"title":"   ","content":"Body","tags":[]}""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private static Note note() {
        return new Note(NOTE_ID, "My note", "Body", CREATED_AT, Set.of("work"));
    }

    @Test
    @DisplayName("POST /notes with a valid body returns 201 and a Location header")
    void postValidReturns201AndLocation() throws Exception {
        when(noteService.create(anyString(), any(), anySet())).thenReturn(note());

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.endsWith("/notes/" + NOTE_ID)))
                .andExpect(jsonPath("$.id").value(NOTE_ID.toString()));

        verify(noteService).create("My note", "Body", Set.of("work"));
    }

    @Test
    @DisplayName("POST /notes with a blank title returns 400")
    void postBlankTitleReturns400() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_TITLE_BODY))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(noteService);
    }

    @Test
    @DisplayName("GET /notes returns 200 with the note list")
    void listReturns200() throws Exception {
        when(noteService.list()).thenReturn(List.of(note()));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NOTE_ID.toString()));
    }

    @Test
    @DisplayName("GET /notes?tag=work returns 200 and delegates the tag to the service")
    void listByTagDelegatesToService() throws Exception {
        when(noteService.listByTag("work")).thenReturn(List.of(note()));

        mockMvc.perform(get("/notes").param("tag", "work"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NOTE_ID.toString()));

        verify(noteService).listByTag("work");
    }

    @Test
    @DisplayName("GET /notes/{id} returns 200 for an existing note")
    void getFoundReturns200() throws Exception {
        when(noteService.get(NOTE_ID)).thenReturn(note());

        mockMvc.perform(get("/notes/" + NOTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NOTE_ID.toString()));
    }

    @Test
    @DisplayName("GET /notes/{id} returns 404 when the service throws NoteNotFoundException")
    void getMissingReturns404() throws Exception {
        when(noteService.get(NOTE_ID)).thenThrow(new NoteNotFoundException(NOTE_ID));

        mockMvc.perform(get("/notes/" + NOTE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /notes/{id} with a malformed id returns 400")
    void getMalformedIdReturns400() throws Exception {
        mockMvc.perform(get("/notes/not-a-uuid"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(noteService);
    }

    @Test
    @DisplayName("PUT /notes/{id} with a valid body returns 200")
    void putFoundReturns200() throws Exception {
        when(noteService.update(eq(NOTE_ID), anyString(), any(), anySet())).thenReturn(note());

        mockMvc.perform(put("/notes/" + NOTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(NOTE_ID.toString()));
    }

    @Test
    @DisplayName("PUT /notes/{id} with a blank title returns 400")
    void putBlankTitleReturns400() throws Exception {
        mockMvc.perform(put("/notes/" + NOTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BLANK_TITLE_BODY))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(noteService);
    }

    @Test
    @DisplayName("PUT /notes/{id} returns 404 when the service throws NoteNotFoundException")
    void putMissingReturns404() throws Exception {
        when(noteService.update(eq(NOTE_ID), anyString(), any(), anySet()))
                .thenThrow(new NoteNotFoundException(NOTE_ID));

        mockMvc.perform(put("/notes/" + NOTE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /notes/{id} returns 204 and delegates to the service")
    void deleteReturns204() throws Exception {
        mockMvc.perform(delete("/notes/" + NOTE_ID))
                .andExpect(status().isNoContent());

        verify(noteService).delete(NOTE_ID);
    }

    @Test
    @DisplayName("DELETE /notes/{id} returns 404 when the service throws NoteNotFoundException")
    void deleteMissingReturns404() throws Exception {
        doThrow(new NoteNotFoundException(NOTE_ID)).when(noteService).delete(NOTE_ID);

        mockMvc.perform(delete("/notes/" + NOTE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /notes with a blank tag lists all notes instead of filtering")
    void listWithBlankTagListsAllNotes() throws Exception {
        when(noteService.list()).thenReturn(List.of(note()));

        mockMvc.perform(get("/notes").param("tag", "   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(NOTE_ID.toString()));

        verify(noteService).list();
        verify(noteService, never()).listByTag(anyString());
    }

    @Test
    @DisplayName("POST /notes with malformed JSON returns 400")
    void postMalformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(noteService);
    }
}

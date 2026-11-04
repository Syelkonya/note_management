/*
 * Notes service.
 */
package com.example.notes.controller;

import com.example.notes.dto.SimpleNoteMapper;
import com.example.notes.exception.NoteNotFoundException;
import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Tests for note reading in {@link NoteController}.
 *
 * @since 1.0
 */
@WebMvcTest(NoteController.class)
@Import(SimpleNoteMapper.class)
final class NoteControllerReadTest {

    /** Collection path. */
    private static final String PATH = "/notes";

    /** Single note path template. */
    private static final String ONE = "/notes/{id}";

    /** Malformed identifier path. */
    private static final String BAD_ID = "/notes/not-a-uuid";

    /** Sample identifier. */
    private static final UUID ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample note. */
    private static final Note NOTE = new Note(
        NoteControllerReadTest.ID, "My note", "Body", NoteControllerReadTest.CREATED,
        Set.of("work")
    );

    /** MVC mock. */
    @Autowired
    private MockMvc mvc;

    /** Service mock. */
    @MockitoBean
    private NoteService service;

    @Test
    void returnsOkForList() throws Exception {
        Mockito.when(
            this.service.list()
        ).thenReturn(
            List.of(NoteControllerReadTest.NOTE)
        );
        Assertions.assertThat(
            this.get(NoteControllerReadTest.PATH).getStatus()
        ).isEqualTo(
            200
        );
    }

    @Test
    void returnsNotesForList() throws Exception {
        Mockito.when(
            this.service.list()
        ).thenReturn(
            List.of(NoteControllerReadTest.NOTE)
        );
        Assertions.assertThat(
            this.get(NoteControllerReadTest.PATH).getContentAsString()
        ).contains(
            "My note"
        );
    }

    @Test
    void returnsNoteById() throws Exception {
        Mockito.when(
            this.service.get(NoteControllerReadTest.ID)
        ).thenReturn(
            NoteControllerReadTest.NOTE
        );
        Assertions.assertThat(
            this.byId().getContentAsString()
        ).contains(
            "My note"
        );
    }

    @Test
    void returnsNotFoundForMissingNote() throws Exception {
        Mockito.when(
            this.service.get(NoteControllerReadTest.ID)
        ).thenThrow(
            new NoteNotFoundException(NoteControllerReadTest.ID)
        );
        Assertions.assertThat(
            this.byId().getStatus()
        ).isEqualTo(
            404
        );
    }

    @Test
    void returnsBadRequestForMalformedIdentifier() throws Exception {
        Assertions.assertThat(
            this.get(NoteControllerReadTest.BAD_ID).getStatus()
        ).isEqualTo(
            400
        );
    }

    /**
     * Perform a GET request.
     * @param path Request path
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse get(final String path) throws Exception {
        return this.mvc.perform(
            MockMvcRequestBuilders.get(path)
        ).andReturn().getResponse();
    }

    /**
     * Fetch the sample note by identifier.
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse byId() throws Exception {
        return this.mvc.perform(
            MockMvcRequestBuilders.get(
                NoteControllerReadTest.ONE, NoteControllerReadTest.ID
            )
        ).andReturn().getResponse();
    }
}

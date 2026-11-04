/*
 * Notes service.
 */
package com.example.notes.controller;

import com.example.notes.dto.SimpleNoteMapper;
import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Tests for note creation in {@link NoteController}.
 *
 * @since 1.0
 */
@WebMvcTest(NoteController.class)
@Import(SimpleNoteMapper.class)
final class NoteControllerTest {

    /** Collection path. */
    private static final String PATH = "/notes";

    /** Sample identifier. */
    private static final UUID ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample note. */
    private static final Note NOTE = new Note(
        NoteControllerTest.ID, "My note", "Body", NoteControllerTest.CREATED,
        Set.of("work")
    );

    /** Valid request body. */
    private static final String BODY =
        "{\"title\":\"My note\",\"content\":\"Body\",\"tags\":[\"work\"]}";

    /** Body with a blank title. */
    private static final String BLANK =
        "{\"title\":\"   \",\"content\":\"Body\",\"tags\":[]}";

    /** Malformed request body. */
    private static final String MALFORMED = "{\"title\":";

    /** MVC mock. */
    @Autowired
    private MockMvc mvc;

    /** Service mock. */
    @MockitoBean
    private NoteService service;

    @Test
    void returnsCreatedForValidBody() throws Exception {
        Mockito.when(
            this.service.create(Mockito.anyString(), Mockito.any(), Mockito.anySet())
        ).thenReturn(
            NoteControllerTest.NOTE
        );
        Assertions.assertThat(
            this.post(NoteControllerTest.BODY).getStatus()
        ).isEqualTo(
            201
        );
    }

    @Test
    void returnsLocationForCreatedNote() throws Exception {
        Mockito.when(
            this.service.create(Mockito.anyString(), Mockito.any(), Mockito.anySet())
        ).thenReturn(
            NoteControllerTest.NOTE
        );
        Assertions.assertThat(
            this.post(NoteControllerTest.BODY).getHeader("Location")
        ).endsWith(
            String.format("/notes/%s", NoteControllerTest.ID)
        );
    }

    @Test
    void returnsBadRequestForBlankTitle() throws Exception {
        Assertions.assertThat(
            this.post(NoteControllerTest.BLANK).getStatus()
        ).isEqualTo(
            400
        );
    }

    @Test
    void returnsBadRequestForMalformedBody() throws Exception {
        Assertions.assertThat(
            this.post(NoteControllerTest.MALFORMED).getStatus()
        ).isEqualTo(
            400
        );
    }

    /**
     * Perform a POST request.
     * @param body Request body
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse post(final String body) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(
            NoteControllerTest.PATH
        );
        request.contentType(MediaType.APPLICATION_JSON);
        request.content(body);
        return this.mvc.perform(request).andReturn().getResponse();
    }
}

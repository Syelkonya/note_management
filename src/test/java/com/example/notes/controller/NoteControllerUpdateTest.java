/*
 * Notes service.
 */
package com.example.notes.controller;

import com.example.notes.dto.SimpleNoteMapper;
import com.example.notes.exception.NoteNotFoundException;
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
 * Tests for note update and deletion in {@link NoteController}.
 *
 * @since 1.0
 */
@WebMvcTest(NoteController.class)
@Import(SimpleNoteMapper.class)
final class NoteControllerUpdateTest {

    /** Single note path template. */
    private static final String ONE = "/notes/{id}";

    /** Sample identifier. */
    private static final UUID ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample note. */
    private static final Note NOTE = new Note(
        NoteControllerUpdateTest.ID, "My note", "Body", NoteControllerUpdateTest.CREATED,
        Set.of("work")
    );

    /** Valid request body. */
    private static final String BODY =
        "{\"title\":\"My note\",\"content\":\"Body\",\"tags\":[\"work\"]}";

    /** Body with a blank title. */
    private static final String BLANK =
        "{\"title\":\"   \",\"content\":\"Body\",\"tags\":[]}";

    /** MVC mock. */
    @Autowired
    private MockMvc mvc;

    /** Service mock. */
    @MockitoBean
    private NoteService service;

    @Test
    void returnsUpdatedNote() throws Exception {
        Mockito.when(
            this.service.update(Mockito.any(), Mockito.any())
        ).thenReturn(
            NoteControllerUpdateTest.NOTE
        );
        Assertions.assertThat(
            this.put(NoteControllerUpdateTest.BODY).getStatus()
        ).isEqualTo(
            200
        );
    }

    @Test
    void returnsBadRequestForBlankTitleOnUpdate() throws Exception {
        Assertions.assertThat(
            this.put(NoteControllerUpdateTest.BLANK).getStatus()
        ).isEqualTo(
            400
        );
    }

    @Test
    void returnsNotFoundOnUpdate() throws Exception {
        Mockito.when(
            this.service.update(Mockito.any(), Mockito.any())
        ).thenThrow(
            new NoteNotFoundException(NoteControllerUpdateTest.ID)
        );
        Assertions.assertThat(
            this.put(NoteControllerUpdateTest.BODY).getStatus()
        ).isEqualTo(
            404
        );
    }

    @Test
    void returnsNoContentOnDelete() throws Exception {
        Assertions.assertThat(
            this.deleted().getStatus()
        ).isEqualTo(
            204
        );
    }

    @Test
    void returnsNotFoundOnDelete() throws Exception {
        Mockito.doThrow(
            new NoteNotFoundException(NoteControllerUpdateTest.ID)
        ).when(
            this.service
        ).delete(
            NoteControllerUpdateTest.ID
        );
        Assertions.assertThat(
            this.deleted().getStatus()
        ).isEqualTo(
            404
        );
    }

    /**
     * Perform a PUT request.
     * @param body Request body
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse put(final String body) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(
            NoteControllerUpdateTest.ONE, NoteControllerUpdateTest.ID
        );
        request.contentType(MediaType.APPLICATION_JSON);
        request.content(body);
        return this.mvc.perform(request).andReturn().getResponse();
    }

    /**
     * Delete the sample note.
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse deleted() throws Exception {
        return this.mvc.perform(
            MockMvcRequestBuilders.delete(
                NoteControllerUpdateTest.ONE, NoteControllerUpdateTest.ID
            )
        ).andReturn().getResponse();
    }
}

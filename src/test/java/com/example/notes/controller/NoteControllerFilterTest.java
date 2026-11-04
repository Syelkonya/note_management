/*
 * Notes service.
 */
package com.example.notes.controller;

import com.example.notes.dto.SimpleNoteMapper;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Tests for tag filtering in {@link NoteController}.
 *
 * @since 1.0
 */
@WebMvcTest(NoteController.class)
@Import(SimpleNoteMapper.class)
final class NoteControllerFilterTest {

    /** Collection path. */
    private static final String PATH = "/notes";

    /** Sample identifier. */
    private static final UUID ID =
        UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    /** Creation timestamp. */
    private static final Instant CREATED = Instant.parse("2024-01-01T00:00:00Z");

    /** Sample tag. */
    private static final String TAG = "work";

    /** Sample note. */
    private static final Note NOTE = new Note(
        NoteControllerFilterTest.ID, "My note", "Body", NoteControllerFilterTest.CREATED,
        Set.of(NoteControllerFilterTest.TAG)
    );

    /** Blank tag value. */
    private static final String BLANK = "   ";

    /** MVC mock. */
    @Autowired
    private MockMvc mvc;

    /** Service mock. */
    @MockitoBean
    private NoteService service;

    @Test
    void returnsOkForTagFilter() throws Exception {
        Mockito.when(
            this.service.listByTag(NoteControllerFilterTest.TAG)
        ).thenReturn(
            List.of(NoteControllerFilterTest.NOTE)
        );
        Assertions.assertThat(
            this.filtered(NoteControllerFilterTest.TAG).getStatus()
        ).isEqualTo(
            200
        );
    }

    @Test
    void delegatesTagToService() throws Exception {
        Mockito.when(
            this.service.listByTag(NoteControllerFilterTest.TAG)
        ).thenReturn(
            List.of(NoteControllerFilterTest.NOTE)
        );
        this.filtered(NoteControllerFilterTest.TAG);
        Mockito.verify(
            this.service
        ).listByTag(
            NoteControllerFilterTest.TAG
        );
    }

    @Test
    void listsAllNotesForBlankTag() throws Exception {
        Mockito.when(
            this.service.list()
        ).thenReturn(
            List.of(NoteControllerFilterTest.NOTE)
        );
        this.filtered(NoteControllerFilterTest.BLANK);
        Mockito.verify(
            this.service
        ).list();
    }

    @Test
    void doesNotFilterForBlankTag() throws Exception {
        Mockito.when(
            this.service.list()
        ).thenReturn(
            List.of(NoteControllerFilterTest.NOTE)
        );
        this.filtered(NoteControllerFilterTest.BLANK);
        Mockito.verify(
            this.service, Mockito.never()
        ).listByTag(
            Mockito.anyString()
        );
    }

    /**
     * Perform a filtered GET request.
     * @param tag Tag to filter by
     * @return Response
     * @throws Exception If fails
     */
    private MockHttpServletResponse filtered(final String tag) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(
            NoteControllerFilterTest.PATH
        );
        request.param("tag", tag);
        return this.mvc.perform(request).andReturn().getResponse();
    }
}

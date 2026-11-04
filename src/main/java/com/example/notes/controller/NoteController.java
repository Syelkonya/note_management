/*
 * Notes service.
 */
package com.example.notes.controller;

import com.example.notes.dto.NoteMapper;
import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.model.Note;
import com.example.notes.service.NoteService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Thin REST controller for the Notes API.
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public final class NoteController {

    /** Path of a single note. */
    private static final String ID_PATH = "/{id}";

    /** Application service. */
    private final NoteService service;

    /** Transport mapper. */
    private final NoteMapper mapper;

    /**
     * Create a note.
     * @param request Note data
     * @return Created note
     */
    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody final NoteRequest request) {
        final NoteResponse created = this.mapper.toResponse(
            this.service.create(
                request.title(), request.content(), request.tags()
            )
        );
        final ServletUriComponentsBuilder uri =
            ServletUriComponentsBuilder.fromCurrentRequest();
        uri.path(NoteController.ID_PATH);
        return ResponseEntity.created(uri.buildAndExpand(created.id()).toUri()).body(created);
    }

    /**
     * List notes, optionally filtered by tag.
     * @param tag Tag to filter by
     * @return Notes
     */
    @GetMapping
    public List<NoteResponse> list(@RequestParam(required = false) final String tag) {
        final List<Note> notes;
        if (tag == null || tag.isBlank()) {
            notes = this.service.list();
        } else {
            notes = this.service.listByTag(tag);
        }
        return this.mapper.toResponses(notes);
    }

    /**
     * Find a note by identifier.
     * @param id Identifier
     * @return Found note
     */
    @GetMapping(NoteController.ID_PATH)
    public NoteResponse get(@PathVariable final UUID id) {
        return this.mapper.toResponse(this.service.get(id));
    }

    /**
     * Update a note.
     * @param id Identifier
     * @param request New note data
     * @return Updated note
     */
    @PutMapping(NoteController.ID_PATH)
    public NoteResponse update(
        @PathVariable final UUID id,
        @Valid @RequestBody final NoteRequest request
    ) {
        return this.mapper.toResponse(this.service.update(id, request));
    }

    /**
     * Delete a note.
     * @param id Identifier
     * @return Empty response
     */
    @DeleteMapping(NoteController.ID_PATH)
    public ResponseEntity<Void> delete(@PathVariable final UUID id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

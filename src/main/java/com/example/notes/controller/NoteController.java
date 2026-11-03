package com.example.notes.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

import com.example.notes.dto.NoteMapper;
import com.example.notes.dto.NoteRequest;
import com.example.notes.dto.NoteResponse;
import com.example.notes.service.NoteService;

import jakarta.validation.Valid;

/**
 * Thin REST controller for the Notes API: delegates all behaviour to
 * {@link NoteService} and maps domain objects to DTOs.
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    private static final String NOTES_PATH = "/notes/";

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request) {
        NoteResponse created = NoteMapper.toResponse(
                noteService.create(request.title(), request.content(), request.tags()));
        return ResponseEntity.created(URI.create(NOTES_PATH + created.id())).body(created);
    }

    @GetMapping
    public List<NoteResponse> list(@RequestParam(required = false) String tag) {
        if (tag == null || tag.isBlank()) {
            return NoteMapper.toResponse(noteService.list());
        }
        return NoteMapper.toResponse(noteService.listByTag(tag));
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable UUID id) {
        return NoteMapper.toResponse(noteService.get(id));
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable UUID id, @Valid @RequestBody NoteRequest request) {
        return NoteMapper.toResponse(
                noteService.update(id, request.title(), request.content(), request.tags()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

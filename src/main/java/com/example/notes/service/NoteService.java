package com.example.notes.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.notes.model.Note;

/**
 * Application service managing notes.
 */
public interface NoteService {

    Note create(String title, String content, Set<String> tags);

    Note get(UUID id);

    List<Note> list();

    List<Note> listByTag(String tag);

    Note update(UUID id, String title, String content, Set<String> tags);

    void delete(UUID id);
}

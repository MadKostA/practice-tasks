package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;

import java.net.URI;

public interface NoteService {

    URI create(NoteRequestDto noteDto);

    NoteResponseDto update(Long id, NoteRequestDto noteDto);

    NoteResponseDto get(Long id);

    void delete(Long id);

    long getTotalNotesCount();

}

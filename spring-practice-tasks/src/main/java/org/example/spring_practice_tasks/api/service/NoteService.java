package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public interface NoteService {

    URI create(NoteRequestDto noteDto);

    void createBatch(List<NoteRequestDto> notesList);

    NoteResponseDto update(UUID id, NoteRequestDto noteDto);

    NoteResponseDto get(UUID id);

    void delete(UUID id);

    long getTotalNotesCount();

}

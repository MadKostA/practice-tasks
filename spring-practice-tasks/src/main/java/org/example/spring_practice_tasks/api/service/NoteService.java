package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.api.dto.NoteAuthorStatsResponseDto;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public interface NoteService {

    URI create(NoteRequestDto noteDto, String author);

    void createBatch(List<NoteRequestDto> notesList, String author);

    NoteResponseDto update(UUID id, NoteRequestDto noteDto, String author);

    NoteResponseDto get(UUID id);

    NoteAuthorStatsResponseDto getStatsByAuthor(String author);

    void delete(UUID id);

    long getTotalNotesCount();

}

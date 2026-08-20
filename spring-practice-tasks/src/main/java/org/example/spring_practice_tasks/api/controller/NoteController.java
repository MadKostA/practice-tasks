package org.example.spring_practice_tasks.api.controller;

import jakarta.validation.Valid;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public interface NoteController {

    @PostMapping(UrlConstants.NOTE_URL)
    ResponseEntity<NoteResponseDto> create(@Valid @RequestBody NoteRequestDto noteDto);

    @PostMapping(UrlConstants.NOTE_BATCH_URL)
    ResponseEntity<NoteResponseDto> createBatch(@Valid @RequestBody List<NoteRequestDto> notesList);

    @PutMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<NoteResponseDto> update(@Valid @RequestBody NoteRequestDto noteDto, @PathVariable UUID id);

    @GetMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<NoteResponseDto> get(@PathVariable UUID id);

    @DeleteMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<Void> delete(@PathVariable UUID id);

    @GetMapping(UrlConstants.NOTE_EXPORT_URL)
    ResponseEntity<byte[]> export(@RequestParam String format);

}

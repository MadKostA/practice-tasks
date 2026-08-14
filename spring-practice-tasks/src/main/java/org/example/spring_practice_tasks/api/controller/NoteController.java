package org.example.spring_practice_tasks.api.controller;

import jakarta.validation.Valid;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface NoteController {

    @PostMapping(UrlConstants.NOTE_URL)
    ResponseEntity<NoteResponseDto> create(@Valid @RequestBody NoteRequestDto noteDto);

    @PutMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<NoteResponseDto> update(@Valid @RequestBody NoteRequestDto noteDto, @PathVariable Long id);

    @GetMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<NoteResponseDto> get(@PathVariable Long id);

    @DeleteMapping(UrlConstants.NOTE_WITH_ID_URL)
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping(UrlConstants.NOTE_EXPORT_URL)
    ResponseEntity<byte[]> export(@RequestParam String format);

}

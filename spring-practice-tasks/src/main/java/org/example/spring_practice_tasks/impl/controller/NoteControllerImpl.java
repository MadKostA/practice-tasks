package org.example.spring_practice_tasks.impl.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.controller.NoteController;
import org.example.spring_practice_tasks.api.dto.*;
import org.example.spring_practice_tasks.api.enums.NoteExportFormat;
import org.example.spring_practice_tasks.api.exceptions.NotValidFormatException;
import org.example.spring_practice_tasks.api.service.ExportService;
import org.example.spring_practice_tasks.api.service.NoteService;
import org.example.spring_practice_tasks.api.service.RevisionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class NoteControllerImpl implements NoteController {

    private final NoteService noteService;
    private final RevisionService revisionService;
    private final ExportService exportService;

    public NoteControllerImpl(NoteService noteService, RevisionService revisionService, ExportService exportService) {
        this.noteService = noteService;
        this.revisionService = revisionService;
        this.exportService = exportService;
    }

    @Override
    public ResponseEntity<NoteResponseDto> create(NoteRequestDto noteRequestDto) {
        log.info("Request to save note with title '{}'", noteRequestDto.title());

        URI location = noteService.create(noteRequestDto);

        log.info("Request to save note with title '{}' was finished", noteRequestDto.title());
        return ResponseEntity
                .created(location)
                .build();
    }

    @Override
    public ResponseEntity<NoteResponseDto> createBatch(List<NoteRequestDto> notesList) {
        log.info("Request to save list of notes, list size={}", notesList.size());

        noteService.createBatch(notesList);

        log.info("Request to save list of notes was finished");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<NoteResponseDto> update(NoteRequestDto noteDto, UUID id) {
        log.info("Request to update note with id={}", id);

        NoteResponseDto updatedNote = noteService.update(id, noteDto);

        log.info("Request to update note with id={} was finished", id);
        return ResponseEntity.ok(updatedNote);
    }

    @Override
    public ResponseEntity<NoteResponseDto> get(UUID id) {
        log.info("Request to get note with id={}", id);

        NoteResponseDto noteResponseDto = noteService.get(id);

        log.info("Request to get note with id={}", id);
        return ResponseEntity.ok(noteResponseDto);
    }

    @Override
    public ResponseEntity<PageResponseDto> getAllHistory(Pageable pageable) {
        log.info("Request to get all notes history");

        PageResponseDto responseDtoList = revisionService.getAllHistory(pageable);

        log.info("Request to get all notes history was finished");
        return ResponseEntity.ok(responseDtoList);
    }

    @Override
    public ResponseEntity<NoteAuthorStatsResponseDto> getStatsByAuthor(String author) {
        log.info("Request for statistics on author={} notes", author);

        NoteAuthorStatsResponseDto responseDto = noteService.getStatsByAuthor(author);

        log.info("Request for statistics on author notes was finished");
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        log.info("Request to delete note with id={}", id);

        noteService.delete(id);

        log.info("Request to delete note with id={}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<byte[]> export(String format) {
        log.info("Request to export notes to format '{}'", format);

        NoteExportFormat noteExportFormat;
        try {
            noteExportFormat = NoteExportFormat.valueOf(format.toUpperCase());
        } catch (RuntimeException e) {
            log.error("Note export format '{}' is invalid", format);
            throw new NotValidFormatException(format);
        }

        byte[] exportedData = exportService.exportNote(noteExportFormat);

        MediaType mediaType = switch (noteExportFormat) {
            case JSON -> MediaType.APPLICATION_JSON;
            case XML -> MediaType.APPLICATION_XML;
            case CSV -> MediaType.TEXT_PLAIN;
        };

        log.info("Request to export notes to format '{}'", format);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=notes." + format.toLowerCase())
                .body(exportedData);
    }
}

package org.example.spring_practice_tasks.impl.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.api.exceptions.NoteNotFoundException;
import org.example.spring_practice_tasks.api.repo.NoteRepository;
import org.example.spring_practice_tasks.api.service.NoteService;
import org.example.spring_practice_tasks.impl.handler.NotesLimitChecker;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.example.spring_practice_tasks.impl.util.NotesConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final Counter notesCreatedCounter;
    private final NoteRepository noteRepository;
    private final NotesLimitChecker notesLimitChecker;

    private final Object lock = new Object();

    public NoteServiceImpl(MeterRegistry meterRegistry,
                           NoteRepository noteRepository,
                           NotesLimitChecker notesLimitChecker) {
        this.notesCreatedCounter = Counter.builder("notes.created")
                .description("Total number of created notes")
                .register(meterRegistry);
        this.noteRepository = noteRepository;
        this.notesLimitChecker = notesLimitChecker;
    }

    @Override
    public URI create(NoteRequestDto noteDto) {
        log.info("Creating new note with title='{}'", noteDto.title());

        NoteModel noteModel = NotesConverter.convertDtoToModel(noteDto);

        long noteId;
        synchronized (lock) {
            long totalNotesCount = getTotalNotesCount();

            notesLimitChecker.checkNotesLimit(totalNotesCount);

            noteId = noteRepository.create(noteModel);
            notesCreatedCounter.increment();
        }

        log.info("Note with title '{}' has been saved with id={}", noteModel.title(), noteId);

        NoteResponseDto createdNote = NotesConverter.convertModelToDto(noteModel, noteId);

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath(UrlConstants.NOTE_WITH_ID_URL)
                .buildAndExpand(createdNote.id())
                .toUri();
    }

    @Override
    public NoteResponseDto update(Long id, NoteRequestDto noteDto) {
        log.info("Updating note with id={}", id);

        NoteModel noteModel = NotesConverter.convertDtoToModel(noteDto);

        NoteModel updatedNote = noteRepository.update(id, noteModel);

        checkNoteModelNull(updatedNote, id);

        log.info("Note with id={} has been updated", id);
        return NotesConverter.convertModelToDto(noteModel, id);
    }

    @Override
    public NoteResponseDto get(Long id) {
        log.info("Getting note with id={}", id);

        NoteModel noteModel = noteRepository.get(id);

        checkNoteModelNull(noteModel, id);

        return NotesConverter.convertModelToDto(noteModel, id);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting note with id={}", id);

        noteRepository.delete(id);

        log.info("Note with id={} has been deleted", id);
    }

    @Override
    public long getTotalNotesCount() {
        log.info("Getting total notes count");

        return noteRepository.getTotalNotesCount();
    }

    private static void checkNoteModelNull(NoteModel noteModel, Long id) {
        if (noteModel == null) {
            log.error("Note with id={} not found", id);
            throw new NoteNotFoundException(id);
        }
    }

}
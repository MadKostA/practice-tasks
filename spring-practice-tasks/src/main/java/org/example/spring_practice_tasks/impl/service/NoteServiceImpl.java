package org.example.spring_practice_tasks.impl.service;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.api.exceptions.NoteNotFoundException;
import org.example.spring_practice_tasks.api.repo.NoteRepository;
import org.example.spring_practice_tasks.api.repo.RevisionRepository;
import org.example.spring_practice_tasks.api.service.NoteService;
import org.example.spring_practice_tasks.impl.config.NoteMapper;
import org.example.spring_practice_tasks.impl.config.RevisionMapper;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.example.spring_practice_tasks.impl.handler.NotesLimitChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final RevisionRepository revisionRepository;
    private final NotesLimitChecker notesLimitChecker;
    private final NoteMapper noteMapper;
    private final RevisionMapper revisionMapper;

    public NoteServiceImpl(NoteRepository noteRepository,
                           RevisionRepository revisionRepository,
                           NotesLimitChecker notesLimitChecker,
                           NoteMapper noteMapper, RevisionMapper revisionMapper) {
        this.noteRepository = noteRepository;
        this.revisionRepository = revisionRepository;
        this.notesLimitChecker = notesLimitChecker;
        this.noteMapper = noteMapper;
        this.revisionMapper = revisionMapper;
    }

    @Override
    public URI create(NoteRequestDto noteRequestDto) {
        log.info("Creating new note with title='{}'", noteRequestDto.title());

        notesLimitChecker.checkNotesLimit(getTotalNotesCount());

        Note note = noteMapper.toEntity(noteRequestDto);
        Note savedNote = noteRepository.save(note);

        log.info("Note with title '{}' has been saved with id={}", savedNote.getTitle(), savedNote.getId());

        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath(UrlConstants.NOTE_WITH_ID_URL)
                .buildAndExpand(savedNote.getId())
                .toUri();
    }

    @Override
    @Transactional
    public void createBatch(List<NoteRequestDto> notesList) {

        if (CollectionUtils.isEmpty(notesList)) {
            throw new IllegalArgumentException("В списке должна присутствовать хотя бы одна заметка");
        }

        long currentTotalNotesCount = getTotalNotesCount();

        for (NoteRequestDto requestDto : notesList) {
            Note entity = noteMapper.toEntity(requestDto);

            notesLimitChecker.checkNotesLimit(currentTotalNotesCount);
            noteRepository.save(entity);

            currentTotalNotesCount++;
        }

    }

    @Override
    @Transactional
    public NoteResponseDto update(UUID id, NoteRequestDto noteRequestDto) {
        log.info("Updating note with id={}", id);

        Note existsNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Not found note with id={}", id);
                    return new NoteNotFoundException(id);
                });

        NoteRevision noteRevision = revisionMapper.noteToEntity(existsNote);
        revisionRepository.save(noteRevision);

        existsNote.addRevision(noteRevision);
        noteMapper.updateNoteFromRequestDto(noteRequestDto, existsNote);
        Note updatedNote = noteRepository.save(existsNote);

        log.info("Note with id={} has been updated", id);
        return noteMapper.toResponseDto(updatedNote);
    }

    @Override
    public NoteResponseDto get(UUID id) {
        log.info("Getting note with id={}", id);

        Note existsNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Not found note with id={}", id);
                    return new NoteNotFoundException(id);
                });

        log.info("Got note with id={}", id);
        return noteMapper.toResponseDto(existsNote);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting note with id={}", id);

        noteRepository.deleteById(id);

        log.info("Note with id={} has been deleted", id);
    }

    @Override
    public long getTotalNotesCount() {
        log.info("Getting total notes count");

        return noteRepository.count();
    }

}
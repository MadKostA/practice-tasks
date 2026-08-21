package org.example.spring_practice_tasks.impl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteAuthorStatsResponseDto;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final RevisionRepository revisionRepository;
    private final NotesLimitChecker notesLimitChecker;
    private final NoteMapper noteMapper;
    private final RevisionMapper revisionMapper;
    private final CacheManager cacheManager;

    @Override
    @CacheEvict(cacheNames = "noteStats", key = "#noteRequestDto.author")
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
    @CacheEvict(cacheNames = "noteStats", key = "#notesList.get(0).author")
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
    @CacheEvict(cacheNames = "noteStats", key = "#noteRequestDto.author")
    public NoteResponseDto update(UUID id, NoteRequestDto noteRequestDto) {
        log.info("Updating note with id={}", id);

        Note existsNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Not found note with id={}", id);
                    return new NoteNotFoundException(id);
                });

        NoteRevision noteRevision = revisionMapper.noteToEntity(existsNote);

        existsNote.addRevision(noteRevision);
        noteMapper.updateNoteFromRequestDto(noteRequestDto, existsNote);

        revisionRepository.save(noteRevision);

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
    @Cacheable(cacheNames = "noteStats", key = "#author")
    public NoteAuthorStatsResponseDto getStatsByAuthor(String author) {
        log.info("Get note stats by author={}", author);

        NoteAuthorStatsResponseDto statsByAuthor = noteRepository.findStatsByAuthor(author);

        log.info("Got note stats by author successfully");
        return statsByAuthor;
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting note with id={}", id);

        Optional<Note> noteOptional = noteRepository.findById(id);
        if (noteOptional.isEmpty()) {
            log.info("Note with id={} not present", id);
            return;
        }

        noteRepository.deleteById(id);

        Cache cache = cacheManager.getCache("noteStats");
        if (cache != null) {
            cache.evict(noteOptional.get().getAuthor());
        }

        log.info("Note with id={} has been deleted", id);
    }

    @Override
    public long getTotalNotesCount() {
        log.info("Getting total notes count");

        return noteRepository.count();
    }

}
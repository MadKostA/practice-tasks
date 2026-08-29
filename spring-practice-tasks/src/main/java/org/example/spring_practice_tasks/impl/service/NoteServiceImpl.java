package org.example.spring_practice_tasks.impl.service;

import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.constants.UrlConstants;
import org.example.spring_practice_tasks.api.dto.NoteAuthorStatsResponseDto;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.api.exceptions.IncorrectAuthorException;
import org.example.spring_practice_tasks.api.exceptions.NoteNotFoundException;
import org.example.spring_practice_tasks.api.service.NoteService;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.example.spring_practice_tasks.impl.handler.NotesLimitChecker;
import org.example.spring_practice_tasks.impl.model.EventType;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.example.spring_practice_tasks.impl.repo.NoteRepository;
import org.example.spring_practice_tasks.impl.repo.RevisionRepository;
import org.example.spring_practice_tasks.impl.service.kafka.NotesSender;
import org.example.spring_practice_tasks.impl.util.NoteMapper;
import org.example.spring_practice_tasks.impl.util.NotesConverter;
import org.example.spring_practice_tasks.impl.util.RevisionMapper;
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
    private final Counter notesCreatedCounter;
    private final NotesSender notesSender;

    @Override
    @CacheEvict(cacheNames = "noteStats", key = "#noteRequestDto.author")
    public URI create(NoteRequestDto noteRequestDto) {
        log.info("Creating new note with title='{}'", noteRequestDto.title());

        notesLimitChecker.checkNotesLimit(getTotalNotesCount());

        Note note = noteMapper.toEntity(noteRequestDto);
        Note savedNote = noteRepository.save(note);

        notesCreatedCounter.increment();

        sendKafkaNoteMessage(savedNote, EventType.CREATED);

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

        notesCreatedCounter.increment(currentTotalNotesCount);
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

        if (!noteRequestDto.author().equals(existsNote.getAuthor())) {
            String author = noteRequestDto.author();
            log.error("Incorrect author '{}'", author);
            throw new IncorrectAuthorException(noteRequestDto.author());
        }

        NoteRevision noteRevision = revisionMapper.noteToEntity(existsNote);

        existsNote.addRevision(noteRevision);
        noteMapper.updateNoteFromRequestDto(noteRequestDto, existsNote);

        revisionRepository.save(noteRevision);

        Note updatedNote = noteRepository.save(existsNote);

        sendKafkaNoteMessage(updatedNote, EventType.UPDATED);

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

        sendKafkaNoteMessage(noteOptional.get(), EventType.DELETED);

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

    private void sendKafkaNoteMessage(Note noteEntity, EventType eventType) {
        NoteEvent noteEvent = NotesConverter.convertEntityToNoteEvent(noteEntity, eventType);
        notesSender.sendMessage(noteEntity.getId(), noteEvent);
    }

}
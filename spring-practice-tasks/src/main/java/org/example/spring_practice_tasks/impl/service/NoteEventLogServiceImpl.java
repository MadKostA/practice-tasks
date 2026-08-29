package org.example.spring_practice_tasks.impl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.service.NoteEventLogService;
import org.example.spring_practice_tasks.impl.entity.NoteEventLog;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.example.spring_practice_tasks.impl.repo.NoteEventLogRepository;
import org.example.spring_practice_tasks.impl.util.NoteEventLogMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteEventLogServiceImpl implements NoteEventLogService {

    private final NoteEventLogMapper noteEventLogMapper;
    private final NoteEventLogRepository noteEventLogRepository;

    @Override
    @Transactional
    public void create(NoteEvent event) {
        UUID eventId = event.eventId();
        UUID noteId = event.noteId();
        log.info("Saving note event log: eventId={}, noteId={}", eventId, noteId);

        NoteEventLog noteEventLog = noteEventLogMapper.noteEventToNoteEventLogEntity(event);

        Optional<NoteEventLog> eventLog = noteEventLogRepository.findById(eventId);

        if (eventLog.isEmpty()) {
            noteEventLogRepository.save(noteEventLog);
        }
    }
}

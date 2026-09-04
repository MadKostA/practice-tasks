package org.example.spring_practice_tasks.impl.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.service.NoteEventLogService;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotesListener {

    private final NoteEventLogService noteEventLogService;

    @KafkaListener(topics = "${kafka.topic.notes.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(NoteEvent event) {
        log.info("Saving note event: eventId={}, noteId={}", event.eventId(), event.noteId());

        noteEventLogService.create(event);

        log.info("Successfully saved note event: eventId={}", event.eventId());
    }

}

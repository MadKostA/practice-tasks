package org.example.spring_practice_tasks.impl.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoteEventDltListener {

    @KafkaListener(topics = "${kafka.topic.notes-dlt.name}", groupId = "note-events-dlt-group",
            idIsGroup = false,
            containerFactory = "noteEventLogKafkaListenerContainerFactory")
    public void listen(NoteEvent event) {
        log.info("DLT received: eventId={}, type={}, noteId={}", event.eventId(), event.eventType(), event.noteId());
    }

}

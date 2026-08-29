package org.example.spring_practice_tasks.impl.model.kafka;

import lombok.Builder;
import org.example.spring_practice_tasks.impl.model.EventType;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NoteEvent(
        UUID noteId,
        String author,
        UUID eventId,
        EventType eventType,
        Instant occurredAt
) {
}

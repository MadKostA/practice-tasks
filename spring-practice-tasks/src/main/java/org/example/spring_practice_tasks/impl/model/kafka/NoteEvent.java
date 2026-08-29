package org.example.spring_practice_tasks.impl.model.kafka;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.example.spring_practice_tasks.impl.model.EventType;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NoteEvent(
        @NotNull(message = "NoteEvent: noteId cannot be null")
        UUID noteId,
        @NotBlank(message = "NoteEvent: author cannot be blank")
        String author,
        @NotNull(message = "NoteEvent: eventId cannot be null")
        UUID eventId,
        @NotNull(message = "NoteEvent: eventType cannot be null")
        EventType eventType,
        Instant occurredAt
) {
}

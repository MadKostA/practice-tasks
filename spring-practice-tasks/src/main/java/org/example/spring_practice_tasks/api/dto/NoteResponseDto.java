package org.example.spring_practice_tasks.api.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NoteResponseDto(
        UUID id,
        String title,
        String text,
        String author,
        Instant createdAt,
        Instant updatedAt
) {
}

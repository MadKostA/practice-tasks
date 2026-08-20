package org.example.spring_practice_tasks.api.dto;

import java.time.Instant;
import java.util.UUID;

public record RevisionResponseDto(
        UUID noteId,
        String oldTitle,
        String oldText,
        Instant changedAt) {
}

package org.example.spring_practice_tasks.api.dto;

import java.time.Instant;

public record NoteAuthorStatsResponseDto(
        long noteCount,
        Instant lastCreatedNoteDate
) {
}

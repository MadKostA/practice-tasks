package org.example.spring_practice_tasks.api.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record NoteResponseDto(
        Long id,
        String title,
        String content,
        Instant createdAt
) {
}

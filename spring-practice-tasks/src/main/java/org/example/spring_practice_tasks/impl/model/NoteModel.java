package org.example.spring_practice_tasks.impl.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record NoteModel(
        String title,
        String content,
        Instant createdAt
) {
}

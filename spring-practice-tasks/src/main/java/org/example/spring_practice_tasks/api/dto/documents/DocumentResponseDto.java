package org.example.spring_practice_tasks.api.dto.documents;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DocumentResponseDto(
        UUID id,
        String body,
        List<String> links,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}

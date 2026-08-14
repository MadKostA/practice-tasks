package org.example.spring_practice_tasks.api.dto.error;

public record FieldErrorDto(
        String field,
        String message
) {
}

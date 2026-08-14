package org.example.spring_practice_tasks.api.dto.error;

import java.util.List;

public record ErrorResponseDto(
        String code,
        String message,
        List<FieldErrorDto> errors
) {
}

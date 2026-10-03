package org.example.spring_practice_tasks.api.dto.documents;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DocumentRequestDto(

        @NotBlank(message = "Body не может быть пустым")
        String body,

        List<String> links
) {
}

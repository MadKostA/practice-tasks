package org.example.spring_practice_tasks.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NoteRequestDto(
        @NotBlank(message = "Заголовок заметки должен должен быть заполнен")
        String title,

        @NotBlank(message = "Текст заметки должен должен быть заполнен")
        String text) {
}

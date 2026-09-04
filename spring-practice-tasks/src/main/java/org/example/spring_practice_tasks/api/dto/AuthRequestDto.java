package org.example.spring_practice_tasks.api.dto;

public record AuthRequestDto(
        String username,
        String password
) {
}

package org.example.spring_practice_tasks.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PageResponseDto(
        List<?> elements,
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<SortResponseDto> sort
) {

}

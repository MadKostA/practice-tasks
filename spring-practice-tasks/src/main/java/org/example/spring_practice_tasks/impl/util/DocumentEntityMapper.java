package org.example.spring_practice_tasks.impl.util;

import org.example.spring_practice_tasks.api.dto.documents.DocumentRequestDto;
import org.example.spring_practice_tasks.api.dto.documents.DocumentResponseDto;
import org.example.spring_practice_tasks.impl.entity.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DocumentEntityMapper {

    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    DocumentEntity toEntity(UUID id, DocumentRequestDto requestDto, String status);

    DocumentResponseDto toResponseDto(DocumentEntity documentEntity);

}

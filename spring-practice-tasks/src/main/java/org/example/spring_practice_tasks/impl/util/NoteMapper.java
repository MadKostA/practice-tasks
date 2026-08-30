package org.example.spring_practice_tasks.impl.util;

import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "version", ignore = true)
    Note toEntity(NoteRequestDto noteRequestDto, String author);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateNoteFromRequestDto(NoteRequestDto noteRequestDto,
                                  @MappingTarget Note note);

    NoteResponseDto toResponseDto(Note note);

    NoteModel toModel(Note note);
}

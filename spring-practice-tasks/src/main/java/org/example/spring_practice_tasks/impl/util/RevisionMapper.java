package org.example.spring_practice_tasks.impl.util;

import org.example.spring_practice_tasks.api.dto.RevisionResponseDto;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.entity.NoteRevision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RevisionMapper {
    @Mapping(target = "noteId", source = "note.id")
    RevisionResponseDto toResponseDto(NoteRevision note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "note", source = "note")
    @Mapping(target = "oldTitle", source = "title")
    @Mapping(target = "oldText", source = "text")
    NoteRevision noteToEntity(Note note);
}

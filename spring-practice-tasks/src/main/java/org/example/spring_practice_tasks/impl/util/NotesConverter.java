package org.example.spring_practice_tasks.impl.util;

import lombok.experimental.UtilityClass;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.impl.model.NoteModel;

import java.time.Instant;

@UtilityClass
public class NotesConverter {

    public static NoteModel convertDtoToModel(NoteRequestDto dto) {
        return NoteModel.builder()
                .title(dto.title())
                .content(dto.content())
                .createdAt(Instant.now())
                .build();
    }

    public static NoteResponseDto convertModelToDto(NoteModel model, long id) {
        return NoteResponseDto.builder()
                .id(id)
                .title(model.title())
                .content(model.content())
                .createdAt(model.createdAt())
                .build();
    }

}

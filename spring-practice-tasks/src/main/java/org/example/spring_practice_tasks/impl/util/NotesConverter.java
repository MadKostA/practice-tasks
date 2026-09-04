package org.example.spring_practice_tasks.impl.util;

import lombok.experimental.UtilityClass;
import org.example.spring_practice_tasks.api.dto.NoteRequestDto;
import org.example.spring_practice_tasks.api.dto.NoteResponseDto;
import org.example.spring_practice_tasks.impl.entity.Note;
import org.example.spring_practice_tasks.impl.model.EventType;
import org.example.spring_practice_tasks.impl.model.NoteModel;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;

import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class NotesConverter {

    public static NoteModel convertDtoToModel(NoteRequestDto dto) {
        return NoteModel.builder()
                .title(dto.title())
                .text(dto.text())
                .createdAt(Instant.now())
                .build();
    }

    public static NoteResponseDto convertModelToDto(NoteModel model, UUID id) {
        return NoteResponseDto.builder()
                .id(id)
                .title(model.title())
                .text(model.text())
                .createdAt(model.createdAt())
                .build();
    }

    public static NoteEvent convertEntityToNoteEvent(Note note, EventType eventType) {
        return NoteEvent.builder()
                .noteId(note.getId())
                .eventType(eventType)
                .eventId(UUID.randomUUID())
                .author(note.getAuthor())
                .occurredAt(note.getCreatedAt())
                .build();
    }

}

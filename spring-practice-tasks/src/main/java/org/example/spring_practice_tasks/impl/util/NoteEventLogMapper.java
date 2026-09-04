package org.example.spring_practice_tasks.impl.util;

import org.example.spring_practice_tasks.impl.entity.NoteEventLog;
import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteEventLogMapper {

    @Mapping(target = "eventType",
            expression = "java(noteEvent.eventType().name())")
    NoteEventLog noteEventToNoteEventLogEntity(NoteEvent noteEvent);

}

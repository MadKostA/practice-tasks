package org.example.spring_practice_tasks.api.service;

import org.example.spring_practice_tasks.impl.model.kafka.NoteEvent;

public interface NoteEventLogService {

    void create(NoteEvent event);

}

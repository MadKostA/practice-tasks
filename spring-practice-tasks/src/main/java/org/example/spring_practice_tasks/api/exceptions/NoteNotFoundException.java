package org.example.spring_practice_tasks.api.exceptions;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(UUID id) {
        super("Не найдена заметка с id=%s".formatted(id));
    }
}

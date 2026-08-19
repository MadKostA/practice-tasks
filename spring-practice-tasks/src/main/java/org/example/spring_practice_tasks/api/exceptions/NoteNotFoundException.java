package org.example.spring_practice_tasks.api.exceptions;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Не найдена заметка с id=%s".formatted(id));
    }
}

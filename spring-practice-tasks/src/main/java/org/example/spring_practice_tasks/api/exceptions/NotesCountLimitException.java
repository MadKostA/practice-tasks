package org.example.spring_practice_tasks.api.exceptions;

public class NotesCountLimitException extends RuntimeException {
    public NotesCountLimitException(int limit) {
        super("Количество заметок не может превышать %s".formatted(limit));
    }
}

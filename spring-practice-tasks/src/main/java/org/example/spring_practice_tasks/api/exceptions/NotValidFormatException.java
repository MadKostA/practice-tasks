package org.example.spring_practice_tasks.api.exceptions;

public class NotValidFormatException extends RuntimeException {
    public NotValidFormatException(String format) {
        super("Экспорт заметок в формат '%s' не поддерживается".formatted(format));
    }
}

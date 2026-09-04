package org.example.spring_practice_tasks.api.exceptions;

public class IncorrectAuthorException extends RuntimeException {
    public IncorrectAuthorException(String author) {
        super("Неверный автор '%s'. Нет доступа к указанной информации.".formatted(author));
    }
}

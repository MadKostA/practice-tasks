package org.example.practice_tasks_1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ExceptionTasks {

//    16. Метод safeParse(String s), который возвращает Optional
//    вместо исключения. (Integer.parseInt() но без исключения)
    public static Optional<Integer> safeParse(String s) {
        if (s == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

//    17. Свое исключение ValidationException extends RuntimeException с конструктором (message, cause).
//    Реализовать метод parseAge(String) ловит NumberFormatException
//    и пробрасывает ValidationException с причиной.
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static int parseAge(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid age: " + input, e);
        }
    }

//    18. Класс ManagedResource implements AutoCloseable: конструктор печатает "open имя", close() печатает "close имя".
    public static class ManagedResource implements AutoCloseable {
        private final String name;

        public ManagedResource(String name) {
            this.name = name;
            System.out.println("open " + name);
        }

        @Override
        public void close() {
            System.out.println("close " + name);
        }
    }

//    19. Посчитать непустые строки текстового файла. (Метод принимает путь к файлу)
    public static long countNonEmptyLines(String filePath) throws IOException {
        try (var lines = Files.lines(Path.of(filePath))) {
            return lines
                    .filter(line -> !line.isBlank()) // Java 11+: строка не пустая и не из пробелов
                    .count();
        }
    }
}

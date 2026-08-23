package org.example.spring_practice_tasks.api.advice;

import lombok.extern.slf4j.Slf4j;
import org.example.spring_practice_tasks.api.dto.error.ErrorResponseDto;
import org.example.spring_practice_tasks.api.dto.error.FieldErrorDto;
import org.example.spring_practice_tasks.api.exceptions.NotValidFormatException;
import org.example.spring_practice_tasks.api.exceptions.NoteNotFoundException;
import org.example.spring_practice_tasks.api.exceptions.NotesCountLimitException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleNotValidException(MethodArgumentNotValidException e) {

        List<FieldErrorDto> validationErrorsList = e.getBindingResult().getFieldErrors()
                .stream()
                .map( fieldError -> new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ErrorResponseDto error = new ErrorResponseDto("VALIDATION_ERROR",
                "Поля содержат невалидные данные",
                validationErrorsList);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(value = {NotValidFormatException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDto> handleNotValidFormat(RuntimeException e) {
        ErrorResponseDto error = new ErrorResponseDto("BAD_REQUEST",
                e.getMessage(), List.of());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NoteNotFoundException e) {
        ErrorResponseDto error = new ErrorResponseDto("NOT_FOUND",
                e.getMessage(), List.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(NotesCountLimitException.class)
    public ResponseEntity<ErrorResponseDto> handleNotesCountLimitException(NotesCountLimitException e) {
        ErrorResponseDto error = new ErrorResponseDto("CONFLICT",
                e.getMessage(), List.of());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDto> handleNotesCountLimitException(OptimisticLockingFailureException e) {
        ErrorResponseDto error = new ErrorResponseDto("CONFLICT",
                e.getMessage(), List.of());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception e) {
        log.error("Some exception has been thrown: {}", e.getMessage());

        ErrorResponseDto error = new ErrorResponseDto("INTERNAL_ERROR",
                "Произошла ошибка на сервере: "  + e.getMessage(), List.of());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

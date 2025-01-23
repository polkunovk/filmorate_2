package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleNotFound(NotFoundException ex) {
        log.error("Данные не найдены: {}", ex.getMessage(), ex);
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails handleInternalServerException(InternalServerException ex) {
        log.error("Ошибка сервера: {}", ex.getMessage(), ex);
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleValidationException(ValidationException ex) {
        log.error("Ошибка валидации: {}", ex.getMessage(), ex);
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorDetails handleConditionsNotMetException(ConditionsNotMetException ex) {
        log.error("Условия не выполнены: {}", ex.getMessage(), ex);
        return new ErrorDetails(ex.getMessage());
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDetails handleDuplicatedDataException(DuplicatedDataException ex) {
        log.error("Данные дублируются: {}", ex.getMessage(), ex);
        return new ErrorDetails(ex.getMessage());
    }

    public static class ErrorDetails {
        private final String message;

        public ErrorDetails(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

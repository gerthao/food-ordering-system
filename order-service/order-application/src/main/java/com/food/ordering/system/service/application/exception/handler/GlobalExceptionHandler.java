package com.food.ordering.system.service.application.exception.handler;

import com.food.ordering.system.application.exception.handler.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handle(Exception exception) {
        log.error(exception.getMessage(), exception);

        return ErrorDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected error!")
                .build();
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handle(ValidationException exception) {
        Function<String, ErrorDTO> createErrorDTO = (String message) -> ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .build();

        return switch (Objects.requireNonNull(exception)) {
            case ConstraintViolationException e -> {
                String violations = extractViolationsFromException(e);
                log.error(violations, e);
                yield createErrorDTO.apply(violations);
            }
            default -> {
                log.error(exception.getMessage());
                yield createErrorDTO.apply(exception.getMessage());
            }
        };
    }

    private String extractViolationsFromException(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("--"));
    }
}

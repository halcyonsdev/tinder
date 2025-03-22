package com.halcyon.tinder.userservice.exception.handler;

import com.halcyon.tinder.userservice.dto.error.ErrorDetailsResponse;
import com.halcyon.tinder.userservice.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDetailsResponse> handleUserExceptions(ApiException e) {
        HttpStatus status = e.getClass().getAnnotation(ResponseStatus.class).value();
        String message = e.getMessage();

        var errorDetails = ErrorDetailsResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(e.getClass().getSimpleName())
                .message(message)
                .build();

        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(Exception ex) {
        List<Map<String, String>> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException) {
            errors = getMethodArgumentNotValidErrors((MethodArgumentNotValidException) ex);
        } else if (ex instanceof ConstraintViolationException) {
            errors = getConstraintViolationErrors((ConstraintViolationException) ex);
        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            errors = getMethodArgumentTypeMismatchErrors((MethodArgumentTypeMismatchException) ex);
        }

        Map<String, Object> response = Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    private List<Map<String, String>> getMethodArgumentNotValidErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", toSnakeCase(error.getField()),
                        "message", Objects.requireNonNull(error.getDefaultMessage())))
                .sorted(Comparator.comparing(e -> e.get("field")))
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getConstraintViolationErrors(ConstraintViolationException ex) {
        return ex.getConstraintViolations().stream()
                .map(violation -> Map.of(
                        "field", toSnakeCase(violation.getPropertyPath().toString()),
                        "message", violation.getMessage()))
                .sorted(Comparator.comparing(e -> e.get("field")))
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getMethodArgumentTypeMismatchErrors(MethodArgumentTypeMismatchException ex) {
        String parameter = ex.getName();
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: '%s'",
                ex.getValue(),
                toSnakeCase(parameter),
                ex.getRequiredType() != null
                        ? ex.getRequiredType().getSimpleName()
                        : "Unknown");

        return List.of(Map.of(
                "field", toSnakeCase(parameter),
                "message", message));
    }

    private String toSnakeCase(String original) {
        return original
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsResponse> handleAllExceptions(Exception e) {
        log.error(e.getMessage(), e);

        String message = "Something went wrong";

        var errorDetails = ErrorDetailsResponse.builder()
                .timestamp(Instant.now())
                .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                .error("InternalServerError")
                .message(message)
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

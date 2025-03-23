package com.halcyon.tinder.userservice.exception.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.halcyon.tinder.userservice.dto.error.ErrorDetailsResponse;
import com.halcyon.tinder.userservice.dto.error.ValidationErrorsResponse;
import com.halcyon.tinder.userservice.exception.ApiException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class })
    public ResponseEntity<ValidationErrorsResponse> handleValidationExceptions(Exception ex) {
        List<Map<String, String>> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException exception) {
            errors = getMethodArgumentNotValidErrors(exception);
        } else if (ex instanceof ConstraintViolationException exception) {
            errors = getConstraintViolationErrors(exception);
        } else if (ex instanceof MethodArgumentTypeMismatchException exception) {
            errors = getMethodArgumentTypeMismatchErrors(exception);
        } else if (ex instanceof HttpMessageNotReadableException exception) {
            errors = getJsonParseErrors(exception);
        }

        var validationErrorsResponse = ValidationErrorsResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(validationErrorsResponse);
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

    private List<Map<String, String>> getJsonParseErrors(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException formatException) {
            String fieldName = formatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));

            String message = String.format(
                    "Invalid value '%s' for field '%s'",
                    formatException.getValue(),
                    fieldName);

            if (formatException.getTargetType().isEnum()) {
                message += String.format(". Expected one of: %s",
                        Arrays.toString(formatException.getTargetType().getEnumConstants()));
            }

            return List.of(Map.of(
                    "field", fieldName,
                    "message", message));
        }

        return List.of(Map.of(
                "field", "unknown",
                "message", "Invalid JSON format or unexpected value."));
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

package com.devsu.fintech.banking_api.excepcion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manage all exceptions extends to BankingException.
     */
    @ExceptionHandler(BankingException.class)
    public ResponseEntity<ApiError> handleBankingException(
            BankingException exception,
            HttpServletRequest request
    ) {
        return build(
                exception.getStatus(),
                exception.getMessage(),
                request,
                exception.getCode(),
                null
        );
    }

    /**
     * Manage resource duplicates.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(
            DuplicateResourceException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request,
                "RESOURCE_DUPLICATE",
                null
        );
    }

    /**
     * Manage validation fails in @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation errors",
                request,
                "VALIDATION_ERROR",
                errors
        );
    }

    /**
     * Manage validation fails in @PathVariable and @RequestParam.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation errors",
                request,
                "CONSTRAINT_VIOLATION",
                errors
        );
    }

    /**
     * Manage errors invalid arguments.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegal(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                "ILLEGAL_ARGUMENT",
                null
        );
    }

    /**
     * Manage any exception.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal error: " + exception.getMessage(),
                request,
                "UNEXPECTED_ERROR",
                null
        );
    }

    /**
     * Method to build the ApiError.
     */
    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            String code,
            List<String> details
    ) {
        ApiError error = ApiError.builder()
                .code(code)
                .message(message)
                .status(status.value())
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}

package com.ELSystem.elsystem.exception;

import com.ELSystem.elsystem.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ── 1. Validation errors (@Valid fails) ──────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err-> ErrorResponse.FieldError.builder()
                        .field(err.getField())
                        .rejectedValue(
                                err.getRejectedValue() != null
                                ? err.getRejectedValue().toString()
                                        : null
                        )
                        .message(err.getDefaultMessage())
                        .build())
                .toList();

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .status(400)
                        .error("Validation Failed")
                        .message("One or more fields are invalid")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .fieldErrors(fieldErrors)
                        .build()
        );
    }


    // ── 2. Resource not found ─────────────────────────────────────────────
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .status(404)
                        .error("Not Found")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ── 3. Invalid transaction (negative balance, etc.) ───────────────────
    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransaction(
            InvalidTransactionException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ErrorResponse.builder()
                        .status(422)
                        .error("Invalid Transaction")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ── 4. Duplicate resource (email/category already exists) ─────────────
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.builder()
                        .status(409)
                        .error("Conflict")
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ── 5. Catch-all for anything unexpected ──────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .status(500)
                        .error("Internal Server Error")
                        .message("Something went wrong. Please try again.")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

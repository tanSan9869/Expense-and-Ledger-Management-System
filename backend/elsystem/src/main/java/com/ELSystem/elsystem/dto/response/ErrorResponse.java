package com.ELSystem.elsystem.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private List<FieldError> fieldErrors;

    @Data
    @Builder
    public static class FieldError{
        private String field;
        private String rejectedValue;
        private String message;
    }
}

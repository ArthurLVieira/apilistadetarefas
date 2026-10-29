package com.project.apilistadetarefas.exception;

import com.project.apilistadetarefas.dto.response.ApiErroResponse;
import com.project.apilistadetarefas.dto.response.ApiResponse;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        List<ApiErroResponse> errors = List.of(
                ApiErroResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build()
        );

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .errors(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

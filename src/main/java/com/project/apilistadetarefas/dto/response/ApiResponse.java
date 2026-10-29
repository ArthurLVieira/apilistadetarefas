package com.project.apilistadetarefas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse <T> {
    private Boolean success;
    private T data;
    private List<ApiErroResponse> errors;
    private String message;
    private String path;
    private String traceId;
    private LocalDateTime timestamp;
}

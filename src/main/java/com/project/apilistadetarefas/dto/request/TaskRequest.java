package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequest (
    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    String title,
    @Size(max = 5000, message = "Descrição deve ter no máximo 5000 caracteres")
    String description,
    LocalDateTime dueDate,
    Long priorityId,
    Long categoryId
) {}

package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.NotNull;

public record TaskCompletionRequest(
    @NotNull(message = "Campo 'completed' é obrigatório")
    Boolean completed
) {}

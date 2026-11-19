package com.project.apilistadetarefas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record TaskReorderRequest(
    @NotEmpty(message = "Lista de reordenação não pode ser vazia")
    @Valid
    List<TaskOrderItem> items
) {
    public record TaskOrderItem(
            @NotNull(message = "id é obrigatório") Long id,
            @NotNull @PositiveOrZero(message = "position deve ser ≥ 0") Integer position
    ) {}
}

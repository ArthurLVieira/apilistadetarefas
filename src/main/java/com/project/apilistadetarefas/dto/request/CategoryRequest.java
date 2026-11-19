package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 1, max = 50, message = "Nome deve ter entre 1 e 50 caracteres")
    private String name;
    private String description;
    @Pattern(
            regexp = "^$|^#[0-9A-Fa-f]{6}$",
            message = "Cor deve estar no formato #RRGGBB"
    )
    private String color;
}

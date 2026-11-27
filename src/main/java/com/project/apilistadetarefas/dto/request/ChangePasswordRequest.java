package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "A senha atual é obrigatória")
        String currentPassword,
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, message = "A nova senha deve conter no mínimo 8 caracteres")
        String newPassword
) {
}
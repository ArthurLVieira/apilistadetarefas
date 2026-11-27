package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        String email,
        @NotBlank(message = "O código de verificação é obrigatório")
        @Pattern(regexp = "\\d{6}", message = "O código deve conter 6 dígitos")
        String code,
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, message = "A nova senha deve conter no mínimo 8 caracteres")
        String newPassword
) {
}
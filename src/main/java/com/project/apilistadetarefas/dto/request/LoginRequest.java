package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "email obrigatório")
    @Email(message = "tipo inválido para email")
    private String email;
    @NotEmpty(message = "senha obrigatória")
    @Size(min = 8, message = "Mínimo de 8 caracteres para senha")
    private String password;
}

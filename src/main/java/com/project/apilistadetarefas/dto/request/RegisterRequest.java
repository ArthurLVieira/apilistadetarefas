package com.project.apilistadetarefas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Campo nome obrigatório para registro")
    private String name;
    @NotBlank(message = "Campo email obrigatório para registro")
    private String email;
    @NotBlank(message = "Campo senha obrigatório para registro")
    @Size(min = 8, message = "Campo senha deve conter no mínimo 8 caracteres")
    private String password;

}

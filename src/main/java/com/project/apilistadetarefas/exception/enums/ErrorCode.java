package com.project.apilistadetarefas.exception.enums;

import com.project.apilistadetarefas.dto.response.ApiErroResponse;
import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("INT-001", "Erro interno no servidor"),
    USER_NOT_FOUND("USER-001", "Usuário não encontrado"),
    USER_NOT_AUTHENTICATED("USER-001", "Usuário não autenticado"),
    USER_ALREADY_EXISTS("USER-002", "Usuário já cadastrado na base de dados"),
    USER_INVALID_EMAIL_OR_PASSWORD("USER-003", "E-mail ou senha inválidos."),
    USER_INVALID_CURRENT_PASSWORD("USER-004", "A senha atual está incorreta."),
    USER_INVALID_PASSWORD_RESET_CODE("USER-005", "Código de recuperação inválido ou expirado."),
    CATEGORY_NOT_FOUND("CAT-001", "Categoria não encontrada"),
    CATEGORY_ALREADY_EXISTS("CAT-002", "Categoria já cadastrado na base de dados"),
    CATEGORY_NAME_ALREADY_EXISTS("CAT-003", "Categoria com mesmo nome já cadastrada no base de dados"),
    CATEGORY_LIMIT_REACHED("CAT-004", "Você atingiu o limite de categorias."),
    CATEGORY_COLOR_ALREADY_EXISTS("CAT-005", "Cor já utilizada por outra categoria deste usuário."),
    TOKEN_INVALID("TKN-001", "Refresh token invalido"),
    TOKEN_INVALID_SESSION("TKN-002", "Sessão inválida. Faça login novamente."),
    PRIORITY_NOT_FOUNT("PRY-001", "Prioridade não encontrada"),
    TASK_NOT_FOUND("TSK-001", "Task não encontrada."),;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.message = message;
        this.code = code;
    }

    public ApiErroResponse toError() {
        return new ApiErroResponse(this.code, this.message);
    }
}

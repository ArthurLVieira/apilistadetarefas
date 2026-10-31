package com.project.apilistadetarefas.exception.enums;

import com.project.apilistadetarefas.dto.response.ApiErroResponse;
import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("INT-001", "Erro interno no servidor"),
    USER_NOT_FOUND("USER-001", "Usuário não encontrado"),
    USER_NOT_AUTHENTICATED("USER-001", "Usuário não autenticado"),
    USER_ALREADY_EXISTS("USER-002", "Usuário já cadastrado na base de dados"),
    CATEGORY_NOT_FOUND("CAT-001", "Categoria não encontrada"),
    CATEGORY_ALREADY_EXISTS("CAT-002", "Categoria já cadastrado na base de dados"),
    CATEGORY_NAME_ALREADY_EXISTS("CAT-003", "Categoria com mesmo nome já cadastrada no base de dados"),;

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

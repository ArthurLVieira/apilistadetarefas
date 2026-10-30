package com.project.apilistadetarefas.exception.enums;

import com.project.apilistadetarefas.dto.response.ApiErroResponse;
import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("INT-001", "Erro interno no servidor"),
    USER_NOT_AUTHENTICATED("USER-001", "Usuário não autenticado"),
    USER_ALREADY_EXISTS("USER-002", "Usuário já cadastrado na base de dados"),;

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

package com.project.apilistadetarefas.exception.exceptions;

import com.project.apilistadetarefas.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}

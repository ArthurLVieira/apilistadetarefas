package com.project.apilistadetarefas.security.utils;

import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.security.UserDatails.CustomUserDatails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public CustomUserDatails userDatails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null || !(auth.getPrincipal() instanceof CustomUserDatails user)) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        return (CustomUserDatails)user;
    }

    public User entity() {
        return userDatails().getUser();
    }

    public Long id() {
        return userDatails().getId();
    }
}

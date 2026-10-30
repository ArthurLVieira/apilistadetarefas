package com.project.apilistadetarefas.mapper;

import com.project.apilistadetarefas.dto.request.UserRequest;
import com.project.apilistadetarefas.dto.response.UserResponse;
import com.project.apilistadetarefas.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest dto);
    UserResponse toDto(User entity);
}

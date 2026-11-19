package com.project.apilistadetarefas.mapper;

import com.project.apilistadetarefas.dto.response.PriorityResponse;
import com.project.apilistadetarefas.entity.Priority;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriorityMapper {
    PriorityResponse toDto(Priority entity);
}

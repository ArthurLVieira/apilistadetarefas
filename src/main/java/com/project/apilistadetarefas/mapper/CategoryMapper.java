package com.project.apilistadetarefas.mapper;

import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.dto.response.CategoryResponse;
import com.project.apilistadetarefas.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest dto);

    CategoryResponse toDto(Category category);

    default String normalizeColor(String color) {
        if (color == null) return null;
        String trimmed = color.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    default void applayTo(Category entity, CategoryRequest request) {
        entity.setName(normalizeColor(entity.getName()));
        entity.setDescription(normalizeColor(entity.getDescription()));
        entity.setColor(normalizeColor(entity.getColor()));
    }
}

package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.dto.response.CategoryResponse;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.mapper.CategoryMapper;
import com.project.apilistadetarefas.repository.CategoryRepository;
import com.project.apilistadetarefas.service.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final CategoryValidator validator;
    private final CategoryMapper mapper;

    public CategoryResponse findById(Long id) {
        return mapper.toDto(validator.validateEntityById(id));
    }

    public Page<CategoryResponse> search(String search, Pageable pageable) {
        Page<Category> page = null;

        return page.map(mapper::toDto);
    }

    public CategoryResponse insert(CategoryRequest request) {
        var entity = mapper.toEntity(request);
        validator.validateEntity(entity, null);
    }

}

package com.project.apilistadetarefas.service.validator;

import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryRepository repository;

    public Category validateEntityById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public void validateEntity(Category entity, Long id) {
        if (existsByEntity(entity, id)) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        if (existsByNameAndUser(entity.getName(), entity.getUser(), id)) {
            throw new BusinessException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }
    }

    private boolean existsByEntity(Category entity, Long id) {
        Optional<Category> currentEntity = repository.findByNameAndColorAndUser(
                entity.getName(),
                entity.getColor(),
                entity.getUser()
        );

        if (id == null) {
            return currentEntity.isPresent();
        }

        return currentEntity.isPresent() && !currentEntity.get().getId().equals(id);
    }

    private boolean existsByNameAndUser(String name, User user, Long id) {
        Optional<Category> currentEntity = repository.findByNameAndUser(name, user);

        if (id == null) {
            return currentEntity.isPresent();
        }

        return currentEntity.isPresent() && !currentEntity.get().getId().equals(id);
    }
}

package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.dto.response.CategoryResponse;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.CategoryMapper;
import com.project.apilistadetarefas.repository.CategoryRepository;
import com.project.apilistadetarefas.repository.TaskRepository;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.service.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final int MAX_CATEGORIES_PER_USER = 20;

    private final CategoryRepository repository;
    private final CategoryValidator validator;
    private final CategoryMapper mapper;
    private UserRepository userRepository;
    private TaskRepository taskRepository;

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        User user = (User) authentication.getPrincipal();

        assert user != null;
        return userRepository.findById(user.getId()).orElseThrow(() -> new
                BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return repository.findByUserOrderByNameAsc(getUser())
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return mapper.toDto(getOwnerOrThrow(id));
    }

    public CategoryResponse insert(CategoryRequest request) {
        if (repository.countByUser(getUser()) > MAX_CATEGORIES_PER_USER) {
            throw new BusinessException(ErrorCode.CATEGORY_LIMIT_REACHED);
        }

        String normalizedName = request.getName().trim();

        if (repository.existsByUserAndNameIgnoreCase(getUser(), normalizedName)) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category entity = mapper.toEntity(request);
        return mapper.toDto(repository.save(entity));
    }

    public CategoryResponse update(CategoryRequest request, Long id) {
        Category entity = getOwnerOrThrow(id);
        String normalizedName = request.getName().trim();
        if (!entity.getName().equals(normalizedName)
            && repository.existsByUserAndNameIgnoreCaseExcludingId(getUser(), normalizedName, id)
        ) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        mapper.applayTo(entity, request);
        return mapper.toDto(repository.save(entity));
    }

    public void deleteById(Long id) {
        Category entity = getOwnerOrThrow(id);
        taskRepository.clearCategory(entity);
        repository.delete(entity);
    }

    private Category getOwnerOrThrow(Long id) {
        return repository.findByIdAndUser(id, getUser())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

}

package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.CategoryMapper;
import com.project.apilistadetarefas.repository.CategoryRepository;
import com.project.apilistadetarefas.repository.TaskRepository;
import com.project.apilistadetarefas.security.utils.CurrentUser;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    private CategoryRepository repository;
    private CategoryMapper mapper;
    private CurrentUser currentUser;
    private CategoryService service;
    private User user;

    @BeforeEach
    void setUp() {
        repository = mock(CategoryRepository.class);
        mapper = mock(CategoryMapper.class);
        currentUser = mock(CurrentUser.class);
        service = new CategoryService(repository, mapper, currentUser, mock(TaskRepository.class));
        user = User.builder().id(1L).build();
    }

    @Test
    void categoryColorIsNotGloballyUnique() throws NoSuchFieldException {
        assertFalse(Category.class.getDeclaredField("color").getAnnotation(Column.class).unique());
    }

    @Test
    void insertRejectsColorAlreadyUsedByCurrentUser() {
        CategoryRequest request = CategoryRequest.builder().name("Work").color("#737373").build();
        when(mapper.toEntity(request)).thenReturn(new Category());
        when(currentUser.getUser()).thenReturn(user);
        when(repository.countByUser(user)).thenReturn(0L);
        when(repository.existsByUserAndNameIgnoreCase(user, "Work")).thenReturn(false);
        when(repository.existsByUserAndColorIgnoreCase(user, "#737373")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.insert(request));

        assertEquals(ErrorCode.CATEGORY_COLOR_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void updateRejectsColorAlreadyUsedByAnotherCategoryOfCurrentUser() {
        Category category = Category.builder().id(1L).name("Work").color("#000000").user(user).build();
        CategoryRequest request = CategoryRequest.builder().name("Work").color("#737373").build();
        when(currentUser.getUser()).thenReturn(user);
        when(repository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));
        when(repository.existsByUserAndColorIgnoreCaseAndIdNot(user, "#737373", 1L)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.update(request, 1L));

        assertEquals(ErrorCode.CATEGORY_COLOR_ALREADY_EXISTS.getCode(), exception.getCode());
    }

    @Test
    void categoryNameIsUniquePerUser() throws NoSuchFieldException {
        assertFalse(Category.class.getDeclaredField("name").getAnnotation(Column.class).unique());
        assertTrue(Arrays.stream(Category.class.getAnnotation(Table.class).uniqueConstraints())
                .anyMatch(constraint -> Arrays.equals(
                        constraint.columnNames(), new String[]{"user_id", "name"})));
    }

    @Test
    void deleteByIdRunsInsideATransaction() throws NoSuchMethodException {
        assertTrue(CategoryService.class
                .getMethod("deleteById", Long.class)
                .isAnnotationPresent(Transactional.class));
    }
}
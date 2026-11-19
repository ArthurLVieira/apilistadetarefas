package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.TaskReorderRequest;
import com.project.apilistadetarefas.dto.request.TaskRequest;
import com.project.apilistadetarefas.dto.response.TaskResponse;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.Task;
import com.project.apilistadetarefas.entity.User;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.TaskMapper;
import com.project.apilistadetarefas.repository.CategoryRepository;
import com.project.apilistadetarefas.repository.TaskRepository;
import com.project.apilistadetarefas.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PriorityService priorityService;
    private final TaskMapper taskMapper;

    public TaskService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            PriorityService priorityService,
            TaskMapper taskMapper
    ) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.priorityService = priorityService;
        this.taskMapper = taskMapper;
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;

        User user = (User) authentication.getPrincipal();
        assert user != null;

        return userRepository.findById(user.getId()).orElseThrow(() ->
                new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskMapper.toResponse(getOwnedOrThrow(id));
    }

    public TaskResponse insert(TaskRequest request) {
        Priority priority = resolvePriority(request.priorityId());
        Category category = resolveCategory(request.categoryId());

        int nextPosition = taskRepository.maxPositionByUser(getUser()) + 1;

        Task task = Task.builder()
                .user(getUser())
                .category(category)
                .priority(priority)
                .title(request.title())
                .description(trimToNull(request.description()))
                .dueDate(request.dueDate())
                .completed(false)
                .position(nextPosition)
                .build();

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(Boolean completed) {
        List<Task> tasks = (completed == null)
                ? taskRepository.findAllByUserWithRelations(getUser())
                : taskRepository.findByUserAndCompletedWithRelations(getUser(), completed);

        return tasks.stream().map(taskMapper::toResponse).collect(Collectors.toList());
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task currentEntity = getOwnedOrThrow(id);

        Priority priority = resolvePriority(request.priorityId());
        Category category = resolveCategory(request.categoryId());

        currentEntity.setTitle(request.title());
        currentEntity.setDescription(request.description());
        currentEntity.setDueDate(request.dueDate());
        currentEntity.setPriority(priority);
        currentEntity.setCategory(category);

        return taskMapper.toResponse(taskRepository.save(currentEntity));
    }

    public TaskResponse setCompletion(Long id, boolean completed) {
        Task task = getOwnedOrThrow(id);

        if (completed && !task.isCompleted()) {
            task.setCompleted(true);
        } else if (!completed && task.isCompleted()) {
            task.setCompleted(false);
        }

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public void reorder(TaskReorderRequest request) {
        List<Long> ids = request.items().stream()
                .map(TaskReorderRequest.TaskOrderItem::id)
                .toList();

        List<Task> owned = taskRepository.findAllByIdAndUser(ids, getUser());
        if (owned.size() != ids.size()) {
            throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
        }

        Map<Long, Task> byId = owned.stream()
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        for (TaskReorderRequest.TaskOrderItem item : request.items()) {
            byId.get(item.id()).setPosition(item.position());
        }
    }

    public void deleteById(Long id) {
        Task task = getOwnedOrThrow(id);
        taskRepository.delete(task);
    }

    private Task getOwnedOrThrow(Long id) {
        return taskRepository.findByIdAndUser(id, getUser()).orElseThrow(() ->
                new BusinessException(ErrorCode.TASK_NOT_FOUND));
    }

    private Priority resolvePriority(Long priorityId) {
        return (priorityId == null)
                ? priorityService.resolveDefault()
                : priorityService.getByIdOrThrow(priorityId);
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findByIdAndUser(categoryId, getUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

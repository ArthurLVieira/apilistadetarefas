package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.request.TaskReorderRequest;
import com.project.apilistadetarefas.dto.request.TaskRequest;
import com.project.apilistadetarefas.dto.response.TaskResponse;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.Task;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.TaskMapper;
import com.project.apilistadetarefas.repository.CategoryRepository;
import com.project.apilistadetarefas.repository.TaskRepository;
import com.project.apilistadetarefas.repository.UserRepository;
import com.project.apilistadetarefas.security.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityService priorityService;
    private final TaskMapper taskMapper;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskMapper.toResponse(getOwnedOrThrow(id));
    }

    public TaskResponse insert(TaskRequest request) {
        Priority priority = resolvePriority(request.priorityId());
        Category category = resolveCategory(request.categoryId());

        int nextPosition = taskRepository.maxPositionByUser(currentUser.getUser()) + 1;

        Task task = Task.builder()
                .user(currentUser.getUser())
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
                ? taskRepository.findAllByUserWithRelations(currentUser.getUser())
                : taskRepository.findByUserAndCompletedWithRelations(currentUser.getUser(), completed);

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

        List<Task> owned = taskRepository.findAllByIdAndUser(ids, currentUser.getUser());
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
        return taskRepository.findByIdAndUser(id, currentUser.getUser()).orElseThrow(() ->
                new BusinessException(ErrorCode.TASK_NOT_FOUND));
    }

    private Priority resolvePriority(Long priorityId) {
        return (priorityId == null)
                ? priorityService.resolveDefault()
                : priorityService.getByIdOrThrow(priorityId);
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findByIdAndUser(categoryId, currentUser.getUser())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

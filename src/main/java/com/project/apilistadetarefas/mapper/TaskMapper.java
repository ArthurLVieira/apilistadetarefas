package com.project.apilistadetarefas.mapper;

import com.project.apilistadetarefas.dto.response.TaskResponse;
import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime due = task.getDueDate();
        boolean open = !task.isCompleted();

        boolean overdue = open && due != null && due.isBefore(now);
        boolean dueToday = open && due != null && !overdue && due.toLocalDate().isEqual(now.toLocalDate());
        boolean upcoming = open && due != null && !overdue && !dueToday;
        boolean noDueDate = open && due == null;

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCompletedAt(),
                task.getDueDate(),
                toPriority(task.getPriority()),
                toCategory(task.getCategory()),
                task.getPosition(),
                overdue,
                dueToday,
                upcoming,
                noDueDate,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    private TaskResponse.PrioritySummary toPriority(Priority priority) {
        if (priority == null) return null;
        return new TaskResponse.PrioritySummary(
                priority.getId(),
                priority.getName(),
                priority.getColor()
        );
    }

    private TaskResponse.CategorySummary toCategory(Category category) {
        if  (category == null) return null;
        return new TaskResponse.CategorySummary(
                category.getId(),
                category.getName(),
                category.getColor()
        );
    }
}

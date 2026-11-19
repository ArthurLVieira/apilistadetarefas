package com.project.apilistadetarefas.dto.response;

import com.project.apilistadetarefas.entity.enums.PriorityEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse (
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime completedAt,
        LocalDateTime dueDate,
        PrioritySummary priority,
        CategorySummary category,
        int position,

        /* estados derivados (não persistidos) */
        boolean overdue,
        boolean dueToday,
        boolean upcoming,
        boolean noDueDate,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record PrioritySummary(Long id, PriorityEnum priority, String color) {}
    public record CategorySummary(Long id, String name, String color) {}
}

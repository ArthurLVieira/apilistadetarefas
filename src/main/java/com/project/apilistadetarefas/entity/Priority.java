package com.project.apilistadetarefas.entity;

import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "priority", schema = "public")
public class Priority {

    @Id
    @Column(name = "priority_id")
    private int id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private PriorityEnum name;

    @Column(nullable = false, unique = true)
    private String color;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

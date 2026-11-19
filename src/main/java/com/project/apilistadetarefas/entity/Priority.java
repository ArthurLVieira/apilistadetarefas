package com.project.apilistadetarefas.entity;

import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "priority",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_priority_name", columnNames = "name"),
                @UniqueConstraint(name = "uq_priority_color", columnNames = "color")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriorityEnum name;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Priority(String color, int displayOrder) {
        this.color = color;
        this.displayOrder = displayOrder;
    }
}

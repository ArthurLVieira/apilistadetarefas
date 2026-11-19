package com.project.apilistadetarefas.dto.response;

import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriorityResponse {
    private Long id;
    private PriorityEnum name;
    private String color;
    private int displayOrder;
}

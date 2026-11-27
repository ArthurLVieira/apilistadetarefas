package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.TaskCompletionRequest;
import com.project.apilistadetarefas.dto.request.TaskReorderRequest;
import com.project.apilistadetarefas.dto.request.TaskRequest;
import com.project.apilistadetarefas.dto.response.TaskResponse;
import com.project.apilistadetarefas.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Criação e gerenciamento das tarefas do usuário autenticado.")
@SecurityRequirement(name = "bearerAuth")
public class TaskController implements Generic {

    private final TaskService taskService;

    @GetMapping("{id}")
    @Operation(summary = "Buscar tarefa por ID")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar tarefas")
    public ResponseEntity<List<TaskResponse>> findAll(
            @RequestParam(required = false) Boolean complet
    ) {
        List<TaskResponse> entities = taskService.findAll(complet);

        return ResponseEntity.ok(entities);
    }

    @PostMapping
    @Operation(summary = "Criar tarefa")
    public ResponseEntity<TaskResponse> insert(
            @RequestBody TaskRequest taskRequest
    ) {
        TaskResponse entity = taskService.insert(taskRequest);

        return ResponseEntity.created(getUri(entity.id())).body(entity);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar tarefa")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestBody TaskRequest taskRequest
    ) {
        TaskResponse entity = taskService.update(id, taskRequest);

        return ResponseEntity.ok(entity);
    }

    @PatchMapping("/{id}/completion")
    @Operation(summary = "Atualizar status de conclusão da tarefa")
    public ResponseEntity<TaskResponse> setComplete(
            @PathVariable Long id,
            @Valid @RequestBody TaskCompletionRequest request
    ) {
        TaskResponse entity = taskService.setCompletion(id, request.completed());

        return ResponseEntity.ok(entity);
    }

    @PutMapping("/reorder")
    @Operation(summary = "Reordenar tarefas")
    public ResponseEntity<TaskResponse> reorder(
            @Valid
            @RequestBody TaskReorderRequest request
    ) {
        taskService.reorder(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir tarefa")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

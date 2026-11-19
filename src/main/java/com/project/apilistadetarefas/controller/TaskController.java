package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.TaskCompletionRequest;
import com.project.apilistadetarefas.dto.request.TaskReorderRequest;
import com.project.apilistadetarefas.dto.request.TaskRequest;
import com.project.apilistadetarefas.dto.response.TaskResponse;
import com.project.apilistadetarefas.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taks")
@RequiredArgsConstructor
public class TaskController implements Generic {

    private final TaskService taskService;

    @GetMapping("{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll(
            @RequestParam(required = true) Boolean complet
    ) {
        List<TaskResponse> entities = taskService.findAll(complet);

        return ResponseEntity.ok(entities);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> insert(
            @RequestBody TaskRequest taskRequest
    ) {
        TaskResponse entity = taskService.insert(taskRequest);

        return ResponseEntity.created(getUri(entity.id())).body(entity);
    }

    @PutMapping("{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestBody TaskRequest taskRequest
    ) {
        TaskResponse entity = taskService.update(id, taskRequest);

        return ResponseEntity.ok(entity);
    }

    @PatchMapping("/{id}/completion")
    public ResponseEntity<TaskResponse> setComplete(
            @PathVariable Long id,
            @RequestParam TaskCompletionRequest request
    ) {
        TaskResponse entity = taskService.setCompletion(id, request.completed());

        return ResponseEntity.ok(entity);
    }

    @PutMapping("/reorder")
    public ResponseEntity<TaskResponse> reorder(
            @Valid
            @RequestBody TaskReorderRequest request
    ) {
        taskService.reorder(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.dto.response.PriorityResponse;
import com.project.apilistadetarefas.service.PriorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
@RequiredArgsConstructor
@Tag(name = "Prioridades", description = "Consulta das prioridades disponíveis.")
public class PriorityController {

    private final PriorityService service;

    @GetMapping
    @Operation(summary = "Listar prioridades")
    public ResponseEntity<List<PriorityResponse>> list() {
        return ResponseEntity.ok().body(service.findAllOrdered());
    }
}

package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.dto.response.PriorityResponse;
import com.project.apilistadetarefas.service.PriorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/priorities")
@RequiredArgsConstructor
public class PriorityController {

    private final PriorityService service;

    @GetMapping
    public ResponseEntity<List<PriorityResponse>> list() {
        return ResponseEntity.ok().body(service.findAllOrdered());
    }
}

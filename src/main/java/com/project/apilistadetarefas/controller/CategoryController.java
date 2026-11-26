package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.dto.response.CategoryResponse;
import com.project.apilistadetarefas.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController implements Generic {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok().body(service.findAll());
    }
    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Valid @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse response = service.insert(categoryRequest);

        return ResponseEntity.created(getUri(response.getId())).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok().body(service.update(request, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

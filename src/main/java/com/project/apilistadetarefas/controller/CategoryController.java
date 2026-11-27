package com.project.apilistadetarefas.controller;

import com.project.apilistadetarefas.controller.generics.Generic;
import com.project.apilistadetarefas.dto.request.CategoryRequest;
import com.project.apilistadetarefas.dto.response.CategoryResponse;
import com.project.apilistadetarefas.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Operações de gerenciamento de categorias.")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController implements Generic {
    private final CategoryService service;

    @GetMapping
    @Operation(summary = "Listar categorias")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok().body(service.findAll());
    }
    @GetMapping("{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Valid @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar categoria")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest categoryRequest
    ) {
        CategoryResponse response = service.insert(categoryRequest);

        return ResponseEntity.created(getUri(response.getId())).body(response);
    }

    @PutMapping("{id}")
    @Operation(summary = "Atualizar categoria")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok().body(service.update(request, id));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Excluir categoria")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

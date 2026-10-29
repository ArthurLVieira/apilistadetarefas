package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.repositor.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
}

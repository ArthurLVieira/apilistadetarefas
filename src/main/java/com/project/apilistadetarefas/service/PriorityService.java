package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriorityService {

    private final PriorityRepository repository;


}

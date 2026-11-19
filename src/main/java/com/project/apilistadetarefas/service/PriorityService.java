package com.project.apilistadetarefas.service;

import com.project.apilistadetarefas.dto.response.PriorityResponse;
import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import com.project.apilistadetarefas.exception.enums.ErrorCode;
import com.project.apilistadetarefas.exception.exceptions.BusinessException;
import com.project.apilistadetarefas.mapper.PriorityMapper;
import com.project.apilistadetarefas.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriorityService {

    private final PriorityRepository repository;
    private final PriorityMapper mapper;

    @Transactional
    public List<PriorityResponse> findAllOrdered() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Priority getByIdOrThrow(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id não pode ser nulo em getByIdOrThrow");
        }

        return repository.findById(id).orElseThrow(() ->
                new BusinessException(ErrorCode.PRIORITY_NOT_FOUNT));
    }

    @Transactional(readOnly = true)
    public Priority resolveDefault() {
        return repository.findByName(PriorityEnum.MEDIUM)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRIORITY_NOT_FOUNT));
    }
}

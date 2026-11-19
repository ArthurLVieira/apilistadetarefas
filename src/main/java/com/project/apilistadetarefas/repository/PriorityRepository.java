package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Priority;
import com.project.apilistadetarefas.entity.enums.PriorityEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PriorityRepository extends JpaRepository<Priority, Long> {

    Optional<Priority> findByName(PriorityEnum name);

    List<Priority> findAllByOrderByDisplayOrderAsc();

    List<Priority> findAllByOrderByDisplayOrderDesc();
}

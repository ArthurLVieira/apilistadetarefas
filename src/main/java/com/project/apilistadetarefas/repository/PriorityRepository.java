package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriorityRepository extends JpaRepository<Priority, Long> {
}

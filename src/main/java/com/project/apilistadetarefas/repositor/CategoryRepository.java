package com.project.apilistadetarefas.repositor;

import com.project.apilistadetarefas.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}

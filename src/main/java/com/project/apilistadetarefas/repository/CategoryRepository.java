package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByNameAndColorAndUser(String name, String color, User user);

    Optional<Category> findByNameAndUser(String name, User user);
}

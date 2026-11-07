package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

}

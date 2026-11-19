package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Task;
import com.project.apilistadetarefas.entity.Token;
import com.project.apilistadetarefas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenHash(String tokenHash);

    List<Token> findByUserIdAndRevokedFalse(Long userId);

    @Modifying
    @Query("""
        update Token t
            set t.revoked = true, t.revoked =:now
        where t.user.id =:userId
            and t.revoked = false
    """)
    int revokeAllByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    @Modifying
    @Query("delete from Token t where t.expiresAt < :threshold")
    int deleteExpired(@Param("threshold") Instant threshold);

    @Query("""
    SELECT t FROM Task t
      JOIN FETCH t.priority
      LEFT JOIN FETCH t.category
     WHERE t.user.id = :userId
     ORDER BY t.position ASC
    """)
    List<Task> findAllByUserIdWithRelations(@Param("userId") Long userId);
}

package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Token;
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
}

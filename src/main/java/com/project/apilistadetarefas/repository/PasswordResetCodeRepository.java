package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.PasswordResetCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByUser_Id(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select code from PasswordResetCode code where code.user.id = :userId")
    Optional<PasswordResetCode> findByUserIdForUpdate(@Param("userId") Long userId);
}
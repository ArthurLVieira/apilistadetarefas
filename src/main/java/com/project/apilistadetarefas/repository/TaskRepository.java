package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.Task;
import com.project.apilistadetarefas.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUser(Long id, User user);

    @Query("""
        select t from Task t
            join fetch t.priority
            left join fetch t.category
        where t.user = :user
        order by t.completed ASC, t.position ASC
    """)
    List<Task> findAllByUserWithRelations(@Param("user") User user);

    @Query("""
        select t from Task t
        where t.id = :id
            and t.user = :user
    """)
    List<Task> findAllByIdAndUser(
            @Param("ids") List<Long> ids,
            @Param("user") User user);

    @Query(value = """
        select t from Task t
            join fetch t.priority
            left join fetch t.category
        where t.user = :user
            and t.completed = :completed
    """,
    countQuery = """
        select count(t) from Task t
            where t.user = :user and t.completed = :completed
    """)
    Page<Task> findByUserAndCompleted(
            @Param("user") User user,
            @Param("completed") boolean completed,
            Pageable pageable
    );

    long countByUser(User user);

    @Query("select coalesce(max(t.position), 0) from Task t where t.user = :user")
    int maxPositionByUser(@Param("user") User user);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Task t set t.category = null where t.category = :category")
    void clearCategory(@Param("category") Category category);

    @Query("""
        select t from Task t
            join fetch t.priority
            left join fetch t.category
        where t.user = :user
            and t.completed = :completed
        order by t.position asc
    """)
    List<Task> findByUserAndCompletedWithRelations(
            @Param("user")User user,
            @Param("completed") boolean completed
    );
}

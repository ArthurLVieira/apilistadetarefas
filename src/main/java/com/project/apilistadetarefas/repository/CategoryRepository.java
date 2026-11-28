package com.project.apilistadetarefas.repository;

import com.project.apilistadetarefas.entity.Category;
import com.project.apilistadetarefas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByIdAndUser(Long id, User user);

    List<Category> findByUserOrderByNameAsc(User user);

    long countByUser(User user);

    @Query("""
        select case when count(c) > 0 then true else false end
            from Category c
        where c.user = :user
            and lower(c.name) = lower(:name)
    """)
    boolean existsByUserAndNameIgnoreCase(User user, String name);

    boolean existsByUserAndColorIgnoreCase(User user, String color);

    boolean existsByUserAndColorIgnoreCaseAndIdNot(User user, String color, Long id);

    Optional<Category> findByNameAndColorAndUser(String name, String color, User user);

    Optional<Category> findByNameAndUser(String name, User user);

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
      FROM Category c
     WHERE c.user = :user
       AND LOWER(c.name) = LOWER(:name)
       AND c.id <> :excludeId
    """)
    boolean existsByUserAndNameIgnoreCaseExcludingId(
            @Param("user") User user,
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );
}

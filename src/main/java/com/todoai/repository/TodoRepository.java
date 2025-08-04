package com.todoai.repository;

import com.todoai.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Todo> findByUserIdAndIsCompletedOrderByCreatedAtDesc(Long userId, boolean isCompleted);
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId ORDER BY " +
           "CASE t.priority WHEN 'URGENT' THEN 1 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 3 WHEN 'LOW' THEN 4 END, " +
           "t.dueDate ASC NULLS LAST, t.createdAt DESC")
    List<Todo> findByUserIdOrderByPriorityAndDueDate(@Param("userId") Long userId);
}

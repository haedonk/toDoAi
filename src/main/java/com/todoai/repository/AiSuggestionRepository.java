package com.todoai.repository;

import com.todoai.entity.AiSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {
    List<AiSuggestion> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}


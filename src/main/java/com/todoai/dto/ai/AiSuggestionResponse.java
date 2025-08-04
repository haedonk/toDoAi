package com.todoai.dto.ai;

import com.todoai.entity.AiSuggestion;
import com.todoai.entity.Todo;

import java.time.LocalDateTime;

public class AiSuggestionResponse {
    private Long id;
    private String suggestedTask;
    private Todo.Priority priority;
    private LocalDateTime createdAt;

    public AiSuggestionResponse() {}

    public AiSuggestionResponse(AiSuggestion aiSuggestion) {
        this.id = aiSuggestion.getId();
        this.suggestedTask = aiSuggestion.getSuggestedTask();
        this.priority = aiSuggestion.getPriority();
        this.createdAt = aiSuggestion.getCreatedAt();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSuggestedTask() {
        return suggestedTask;
    }

    public void setSuggestedTask(String suggestedTask) {
        this.suggestedTask = suggestedTask;
    }

    public Todo.Priority getPriority() {
        return priority;
    }

    public void setPriority(Todo.Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

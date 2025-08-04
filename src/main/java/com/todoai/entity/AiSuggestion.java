package com.todoai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_suggestions")
public class AiSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 500)
    @Column(name = "suggested_task")
    private String suggestedTask;

    @Enumerated(EnumType.STRING)
    private Todo.Priority priority = Todo.Priority.MEDIUM;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public AiSuggestion() {}

    public AiSuggestion(Long userId, String suggestedTask, Todo.Priority priority) {
        this.userId = userId;
        this.suggestedTask = suggestedTask;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.todoai.service;

import com.todoai.dto.todo.TodoRequest;
import com.todoai.dto.todo.TodoResponse;
import com.todoai.entity.Todo;
import com.todoai.entity.User;
import com.todoai.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public List<TodoResponse> getUserTodos(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return todoRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    public TodoResponse createTodo(TodoRequest todoRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Todo todo = new Todo(
            user.getId(),
            todoRequest.getTitle(),
            todoRequest.getDescription(),
            todoRequest.getPriority(),
            todoRequest.getDueDate()
        );

        Todo savedTodo = todoRepository.save(todo);
        return new TodoResponse(savedTodo);
    }

    public TodoResponse updateTodo(Long todoId, TodoRequest todoRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setPriority(todoRequest.getPriority());
        todo.setDueDate(todoRequest.getDueDate());

        Todo updatedTodo = todoRepository.save(todo);
        return new TodoResponse(updatedTodo);
    }

    public TodoResponse completeTodo(Long todoId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        todo.setCompleted(true);
        Todo updatedTodo = todoRepository.save(todo);
        return new TodoResponse(updatedTodo);
    }

    public void deleteTodo(Long todoId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        todoRepository.delete(todo);
    }

    public List<Todo> getUserTodosForAI(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return todoRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public TodoResponse toggleTodoCompletion(Long todoId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Todo todo = todoRepository.findByIdAndUserId(todoId, user.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        // Toggle the completion status
        todo.setCompleted(!todo.isCompleted());
        Todo updatedTodo = todoRepository.save(todo);
        return new TodoResponse(updatedTodo);
    }
}

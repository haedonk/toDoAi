package com.todoai.controller;

import com.todoai.dto.todo.TodoRequest;
import com.todoai.dto.todo.TodoResponse;
import com.todoai.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getUserTodos(Authentication authentication) {
        List<TodoResponse> todos = todoService.getUserTodos(authentication);
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest todoRequest,
                                                   Authentication authentication) {
        TodoResponse todo = todoService.createTodo(todoRequest, authentication);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id,
                                       @Valid @RequestBody TodoRequest todoRequest,
                                       Authentication authentication) {
        try {
            TodoResponse todo = todoService.updateTodo(id, todoRequest, authentication);
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeTodo(@PathVariable Long id, Authentication authentication) {
        try {
            TodoResponse todo = todoService.completeTodo(id, authentication);
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTodoCompletion(@PathVariable Long id, Authentication authentication) {
        try {
            TodoResponse todo = todoService.toggleTodoCompletion(id, authentication);
            return ResponseEntity.ok(todo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id, Authentication authentication) {
        try {
            todoService.deleteTodo(id, authentication);
            return ResponseEntity.ok(new AuthController.MessageResponse("Todo deleted successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthController.MessageResponse(e.getMessage()));
        }
    }
}

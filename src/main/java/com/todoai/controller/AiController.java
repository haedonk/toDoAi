package com.todoai.controller;

import com.todoai.dto.ai.AiSuggestionResponse;
import com.todoai.dto.todo.TodoResponse;
import com.todoai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private OpenAiService openAiService;

    @PostMapping("/prioritize")
    public ResponseEntity<List<TodoResponse>> prioritizeTodos(Authentication authentication) {
        List<TodoResponse> prioritizedTodos = openAiService.prioritizeTodos(authentication);
        return ResponseEntity.ok(prioritizedTodos);
    }

    @PostMapping("/suggest")
    public ResponseEntity<List<AiSuggestionResponse>> generateSuggestions(Authentication authentication) {
        List<AiSuggestionResponse> suggestions = openAiService.generateSuggestions(authentication);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<AiSuggestionResponse>> getUserSuggestions(Authentication authentication) {
        List<AiSuggestionResponse> suggestions = openAiService.getUserSuggestions(authentication);
        return ResponseEntity.ok(suggestions);
    }
}

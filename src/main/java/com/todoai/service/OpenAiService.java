package com.todoai.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.todoai.dto.ai.AiSuggestionResponse;
import com.todoai.dto.todo.TodoResponse;
import com.todoai.entity.AiSuggestion;
import com.todoai.entity.Todo;
import com.todoai.entity.User;
import com.todoai.repository.AiSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiService {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiService.class);

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Autowired
    private TodoService todoService;

    @Autowired
    private AiSuggestionRepository aiSuggestionRepository;

    private com.theokanning.openai.service.OpenAiService openAiClient;

    private com.theokanning.openai.service.OpenAiService getOpenAiClient() {
        if (openAiClient == null && !"your-openai-api-key".equals(openAiApiKey)) {
            openAiClient = new com.theokanning.openai.service.OpenAiService(openAiApiKey, Duration.ofSeconds(30));
        }
        return openAiClient;
    }

    public List<TodoResponse> prioritizeTodos(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        logger.info("Starting todo prioritization for user: {} (ID: {})", user.getUsername(), user.getId());

        List<Todo> todos = todoService.getUserTodosForAI(authentication);
        logger.debug("Retrieved {} todos for prioritization", todos.size());

        if (todos.isEmpty()) {
            logger.info("No todos found for user: {}, returning empty list", user.getUsername());
            return new ArrayList<>();
        }

        if (getOpenAiClient() == null) {
            logger.warn("OpenAI client not available (API key not configured), using fallback prioritization");
            // Fallback: return todos sorted by existing priority
            List<TodoResponse> fallbackResult = todos.stream()
                    .sorted((t1, t2) -> {
                        int priority1 = getPriorityValue(t1.getPriority());
                        int priority2 = getPriorityValue(t2.getPriority());
                        return Integer.compare(priority1, priority2);
                    })
                    .map(TodoResponse::new)
                    .collect(Collectors.toList());
            logger.debug("Fallback prioritization completed, returning {} todos", fallbackResult.size());
            return fallbackResult;
        }

        try {
            String todosText = todos.stream()
                    .map(todo -> String.format("- %s (Priority: %s, Due: %s)",
                            todo.getTitle(),
                            todo.getPriority(),
                            todo.getDueDate() != null ? todo.getDueDate().toString() : "No due date"))
                    .collect(Collectors.joining("\n"));

            logger.debug("Prepared todos text for OpenAI:\n{}", todosText);

            String prompt = String.format(
                    "Please prioritize the following todos by importance and urgency. " +
                    "Return only the todo titles in order of priority (most important first), " +
                    "one per line, exactly as they appear:\n\n%s", todosText);

            logger.debug("Sending request to OpenAI with prompt length: {}", prompt.length());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4o-mini")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                    .maxTokens(500)
                    .build();

            logger.info("Making OpenAI API call for todo prioritization");
            String response = getOpenAiClient().createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            logger.debug("OpenAI response received: {}", response);
            logger.info("OpenAI prioritization completed successfully for user: {}", user.getUsername());

            List<TodoResponse> result = reorderTodosByAiResponse(todos, response);
            logger.debug("Reordered {} todos based on AI response", result.size());
            return result;

        } catch (Exception e) {
            logger.error("OpenAI prioritization failed for user: {}. Error: {}", user.getUsername(), e.getMessage(), e);
            // Fallback on error
            List<TodoResponse> fallbackResult = todos.stream()
                    .map(TodoResponse::new)
                    .collect(Collectors.toList());
            logger.debug("Using fallback result due to error, returning {} todos", fallbackResult.size());
            return fallbackResult;
        }
    }

    public List<AiSuggestionResponse> generateSuggestions(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        logger.info("Starting AI suggestion generation for user: {} (ID: {})", user.getUsername(), user.getId());

        List<Todo> todos = todoService.getUserTodosForAI(authentication);
        logger.debug("Retrieved {} existing todos for context", todos.size());

        if (getOpenAiClient() == null) {
            logger.warn("OpenAI client not available (API key not configured), using mock suggestions");
            List<AiSuggestionResponse> mockResult = createMockSuggestions(user.getId());
            logger.debug("Generated {} mock suggestions", mockResult.size());
            return mockResult;
        }

        try {
            String todosContext = todos.isEmpty() ? "No existing todos" :
                    todos.stream()
                            .map(todo -> String.format("- %s (%s)", todo.getTitle(), todo.getPriority()))
                            .collect(Collectors.joining("\n"));

            logger.debug("Prepared todos context for OpenAI:\n{}", todosContext);

            String prompt = String.format(
                    "Based on the following existing todos, suggest 3 new productive tasks that would " +
                    "complement this person's workflow. For each suggestion, provide the task and a priority level " +
                    "(LOW, MEDIUM, HIGH, URGENT). Format as: 'TASK_NAME | PRIORITY'\n\n" +
                    "Existing todos:\n%s", todosContext);

            logger.debug("Sending suggestion request to OpenAI with prompt length: {}", prompt.length());

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(Arrays.asList(
                            new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                    .maxTokens(300)
                    .build();

            logger.info("Making OpenAI API call for suggestion generation");
            String response = getOpenAiClient().createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            logger.debug("OpenAI suggestion response received: {}", response);
            logger.info("OpenAI suggestion generation completed successfully for user: {}", user.getUsername());

            List<AiSuggestionResponse> result = parseSuggestionsFromResponse(response, user.getId());
            logger.debug("Parsed {} suggestions from AI response", result.size());
            return result;

        } catch (Exception e) {
            logger.error("OpenAI suggestion generation failed for user: {}. Error: {}", user.getUsername(), e.getMessage(), e);
            List<AiSuggestionResponse> fallbackResult = createMockSuggestions(user.getId());
            logger.debug("Using mock suggestions due to error, returning {} suggestions", fallbackResult.size());
            return fallbackResult;
        }
    }

    public List<AiSuggestionResponse> getUserSuggestions(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        logger.info("Retrieving AI suggestions for user: {} (ID: {})", user.getUsername(), user.getId());

        List<AiSuggestionResponse> suggestions = aiSuggestionRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(AiSuggestionResponse::new)
                .collect(Collectors.toList());

        logger.debug("Retrieved {} existing suggestions for user: {}", suggestions.size(), user.getUsername());
        return suggestions;
    }

    private List<TodoResponse> reorderTodosByAiResponse(List<Todo> todos, String aiResponse) {
        logger.debug("Starting todo reordering based on AI response");
        String[] lines = aiResponse.split("\n");
        logger.debug("AI response contains {} lines", lines.length);

        List<TodoResponse> reorderedTodos = new ArrayList<>();

        for (String line : lines) {
            String cleanLine = line.trim().replaceAll("^[-*]\\s*", "");
            logger.debug("Processing line: '{}' -> '{}'", line, cleanLine);

            for (Todo todo : todos) {
                if (todo.getTitle().equalsIgnoreCase(cleanLine) &&
                    reorderedTodos.stream().noneMatch(t -> t.getId().equals(todo.getId()))) {
                    reorderedTodos.add(new TodoResponse(todo));
                    logger.debug("Matched and added todo: '{}'", todo.getTitle());
                    break;
                }
            }
        }

        // Add any remaining todos that weren't matched
        int unmatchedCount = 0;
        for (Todo todo : todos) {
            if (reorderedTodos.stream().noneMatch(t -> t.getId().equals(todo.getId()))) {
                reorderedTodos.add(new TodoResponse(todo));
                unmatchedCount++;
                logger.debug("Added unmatched todo: '{}'", todo.getTitle());
            }
        }

        logger.debug("Reordering completed: {} matched, {} unmatched, {} total",
                    reorderedTodos.size() - unmatchedCount, unmatchedCount, reorderedTodos.size());
        return reorderedTodos;
    }

    private List<AiSuggestionResponse> parseSuggestionsFromResponse(String response, Long userId) {
        logger.debug("Parsing suggestions from AI response for user ID: {}", userId);
        List<AiSuggestionResponse> suggestions = new ArrayList<>();
        String[] lines = response.split("\n");
        logger.debug("Response contains {} lines to parse", lines.length);

        for (String line : lines) {
            if (line.contains("|")) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String task = parts[0].trim().replaceAll("^\\d+\\.\\s*", "");
                    String priorityStr = parts[1].trim().toUpperCase();

                    logger.debug("Parsing suggestion: task='{}', priority='{}'", task, priorityStr);

                    Todo.Priority priority;
                    try {
                        priority = Todo.Priority.valueOf(priorityStr);
                        logger.debug("Successfully parsed priority: {}", priority);
                    } catch (IllegalArgumentException e) {
                        priority = Todo.Priority.MEDIUM;
                        logger.warn("Invalid priority '{}', defaulting to MEDIUM", priorityStr);
                    }

                    try {
                        AiSuggestion suggestion = new AiSuggestion(userId, task, priority);
                        AiSuggestion saved = aiSuggestionRepository.save(suggestion);
                        suggestions.add(new AiSuggestionResponse(saved));
                        logger.debug("Saved AI suggestion: '{}' with priority {}", task, priority);
                    } catch (Exception e) {
                        logger.error("Failed to save AI suggestion: '{}'. Error: {}", task, e.getMessage());
                    }
                }
            }
        }

        logger.info("Successfully parsed and saved {} suggestions for user ID: {}", suggestions.size(), userId);
        return suggestions;
    }

    private List<AiSuggestionResponse> createMockSuggestions(Long userId) {
        logger.debug("Creating mock suggestions for user ID: {}", userId);
        List<AiSuggestion> mockSuggestions = Arrays.asList(
                new AiSuggestion(userId, "Review and organize your email inbox", Todo.Priority.MEDIUM),
                new AiSuggestion(userId, "Plan tomorrow's priorities", Todo.Priority.HIGH),
                new AiSuggestion(userId, "Take a 15-minute break for mental wellness", Todo.Priority.LOW)
        );

        List<AiSuggestionResponse> result = mockSuggestions.stream()
                .map(suggestion -> {
                    try {
                        AiSuggestion saved = aiSuggestionRepository.save(suggestion);
                        logger.debug("Saved mock suggestion: '{}'", suggestion.getSuggestedTask());
                        return new AiSuggestionResponse(saved);
                    } catch (Exception e) {
                        logger.error("Failed to save mock suggestion: '{}'. Error: {}",
                                   suggestion.getSuggestedTask(), e.getMessage());
                        return null;
                    }
                })
                .filter(suggestion -> suggestion != null)
                .collect(Collectors.toList());

        logger.info("Created {} mock suggestions for user ID: {}", result.size(), userId);
        return result;
    }

    private int getPriorityValue(Todo.Priority priority) {
        switch (priority) {
            case URGENT: return 1;
            case HIGH: return 2;
            case MEDIUM: return 3;
            case LOW: return 4;
            default: return 3;
        }
    }
}

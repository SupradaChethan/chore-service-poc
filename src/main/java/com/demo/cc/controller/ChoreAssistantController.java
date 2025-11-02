package com.demo.cc.controller;

import com.demo.cc.dto.ChatRequest;
import com.demo.cc.dto.ChatResponse;
import com.demo.cc.service.ChoreAssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for AI-powered chore assistant.
 * Provides natural language interaction for managing chores and users.
 */
@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
@Slf4j
public class ChoreAssistantController {

    private final ChoreAssistantService assistantService;

    /**
     * Chat with the AI assistant
     * @param request Chat request containing user message
     * @return AI-generated response
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("POST /api/v1/assistant/chat - Processing chat request");

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Please provide a message."));
        }

        String response = assistantService.step(request.getSessionId(), request.getMessage());
        return ResponseEntity.ok(new ChatResponse(response));
    }

    /**
     * Health check endpoint for the assistant
     * @return Status message
     */
    @GetMapping("/health")
    public ResponseEntity<ChatResponse> health() {
        log.info("GET /api/v1/assistant/health - Health check");
        return ResponseEntity.ok(
                new ChatResponse("Chore Assistant is ready to help! Ask me anything about managing your family chores.")
        );
    }
}

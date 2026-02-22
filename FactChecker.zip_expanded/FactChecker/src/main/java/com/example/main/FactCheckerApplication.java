package com.example.main;

import org.slf4j.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RestController
@RequestMapping("/api/ai")
public class FactCheckerApplication {

    private final ChatModel chatModel; // Autowired by Spring Boot
    private static final Logger log = LoggerFactory.getLogger(FactCheckerApplication.class);

    public FactCheckerApplication(ChatModel chatModel) {
        this.chatModel = chatModel;
        log.info("DemoApplication initialized with Spring AI ChatModel, targeting OpenRouter.");
    }

    public static void main(String[] args) {
        SpringApplication.run(FactCheckerApplication.class, args);
    }

    /**
     * Data Transfer Object for the chat request.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String message;
    }

    /**
     * Internal method to call the AI model.
     */
    private String getChatCompletion(String userMessage) {
        log.info("Sending prompt to OpenRouter model: '{}'", userMessage.substring(0, Math.min(userMessage.length(), 100)) + "...");
        try {
            // The Spring AI ChatModel handles the call to OpenRouter based on application.properties
            return this.chatModel.call(userMessage);
        } catch (Exception e) {
            log.error("Error calling OpenRouter via Spring AI: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get completion from AI service: " + e.getMessage(), e);
        }
    }

    /**
     * REST API endpoint to interact with the AI model.
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chatWithAi(@RequestBody ChatRequest chatRequest) {
        if (chatRequest == null || chatRequest.getMessage() == null || chatRequest.getMessage().isBlank()) {
            log.warn("Received empty or null chat request message.");
            return ResponseEntity.badRequest().body("Message content cannot be empty.");
        }

        log.info("Processing chat request for message: '{}'", chatRequest.getMessage().substring(0, Math.min(chatRequest.getMessage().length(), 100)) + "...");
        try {
            String response = getChatCompletion(chatRequest.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Error already logged in getChatCompletion
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Sorry, an error occurred while communicating with the AI service.");
        }
    }
}
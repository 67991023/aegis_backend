package com.aegis.aiservice.controller;

import com.aegis.aiservice.dto.asisstant;
import com.aegis.aiservice.dto.userGenerateRequest;
import com.aegis.aiservice.service.aiServiceImpt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat-service")
public class Controller {
    private final aiServiceImpt aiService;
    private final asisstant asisstant;

    public Controller(aiServiceImpt aiService, asisstant asisstant) {
        this.aiService = aiService;
        this.asisstant = asisstant;
    }

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody userGenerateRequest request) {
        String message = request.getMessage();
        // Accessing sessionId properly
        String sessionId = request.getSessionId();

        return asisstant.chat(sessionId, message);
    }
}
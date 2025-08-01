package com.aegis.aiservice.controller;

import com.aegis.aiservice.dto.asisstant;
import com.aegis.aiservice.dto.userGenerateRequest;
import com.aegis.aiservice.service.BufferedAzureTtsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/speech-service")
public class SpeechController {

    private final asisstant asisstant;
    private final BufferedAzureTtsService bufferedAzureTtsService;

    @Autowired
    public SpeechController(asisstant asisstant, BufferedAzureTtsService bufferedAzureTtsService) {
        this.asisstant = asisstant;
        this.bufferedAzureTtsService = bufferedAzureTtsService;
    }

    @PostMapping(value = "/chat-and-speak")
    public Mono<ResponseEntity<Resource>> chatAndSpeak(@RequestBody userGenerateRequest request) {
        String message = request.getMessage();
        String sessionId = request.getSessionId();

        // For shorter response, limit the input message length for testing
        if (message.length() > 200) {
            message = message.substring(0, 200);
        }

        String finalMessage = message;

        return asisstant.chat(sessionId, finalMessage)
                .collectList()
                .map(chunks -> String.join("", chunks)) // Join all chunks
                .flatMap(response -> {
                    // For testing, use a shorter response
                    String shortResponse = response.length() > 500 ?
                            response.substring(0, 500) : response;

                    System.out.println("Converting to speech: " + shortResponse);
                    return bufferedAzureTtsService.convertToSpeech(shortResponse, "en-US-JennyNeural");
                })
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"response.mp3\"")
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(resource))
                .onErrorResume(e -> {
                    System.err.println("Error during chat-and-speak: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ByteArrayResource("Error converting to speech".getBytes())));
                });
    }
}
package com.aegis.aiservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aegis.aiservice.dto.EmotionalResponseRequest;
import com.aegis.aiservice.dto.userGenerateRequest; // Add this import
import com.aegis.aiservice.service.EmotionalTTSService;
import com.aegis.aiservice.service.aiServiceImpt;

@RestController
@RequestMapping("/api/speech")
public class EmotionalSpeechController {

    private final EmotionalTTSService ttsService;
    private final aiServiceImpt aiService;

    @Autowired
    public EmotionalSpeechController(EmotionalTTSService ttsService, aiServiceImpt aiService) {
        this.ttsService = ttsService;
        this.aiService = aiService; // Make sure this is aiService, not ttsService
    }

    @PostMapping(value = "/synthesize", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody EmotionalResponseRequest request) {
        // รับข้อความจาก aiService
        String message = request.getMessage();
        String sessionId = request.getSessionId();
        String userEmotion = request.getUserEmotion();
        String preferredGender = request.getPreferredVoiceGender();

        // สร้างการตอบสนองข้อความ (ถ้าไม่มีข้อความในคำขอ)
        if (message == null || message.trim().isEmpty()) {
            // Create a userGenerateRequest object correctly
            userGenerateRequest genRequest = new userGenerateRequest();
            genRequest.setMessage(request.getMessage());
            genRequest.setSessionId(sessionId);

            message = aiService.getMessage(genRequest);
        }

        // แปลงข้อความเป็นเสียงที่มีอารมณ์
        byte[] audioData = ttsService.synthesizeSpeech(message, userEmotion, preferredGender);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.mp3")
                .body(audioData);
    }
}
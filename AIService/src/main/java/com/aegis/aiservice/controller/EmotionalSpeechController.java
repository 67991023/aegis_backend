package com.aegis.aiservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aegis.aiservice.dto.EmotionalResponseRequest;
import com.aegis.aiservice.service.EmotionalTTSService;
// นำเข้า aiService ที่มีอยู่ในโปรเจคของคุณ
import com.aegis.aiservice.service.aiServiceImpt;

@RestController
@RequestMapping("/api/speech")
public class EmotionalSpeechController {

    private final EmotionalTTSService ttsService;
    private final aiServiceImpt aiService;

    @Autowired
    public EmotionalSpeechController(EmotionalTTSService ttsService, aiServiceImpt aiService) {
        this.ttsService = ttsService;
        this.aiService = aiService;
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
            // ใช้ aiService ที่มีอยู่เพื่อสร้างการตอบสนอง
            // โค้ดนี้ต้องปรับให้เข้ากับการใช้งาน aiService ของคุณ
            message = aiService.getMessage(request.getMessage(), sessionId);
        }

        // แปลงข้อความเป็นเสียงที่มีอารมณ์
        byte[] audioData = ttsService.synthesizeSpeech(message, userEmotion, preferredGender);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.mp3")
                .body(audioData);
    }
}
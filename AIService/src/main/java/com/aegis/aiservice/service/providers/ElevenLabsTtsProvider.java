package com.aegis.aiservice.service.providers;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class ElevenLabsTtsProvider implements TtsProvider {

    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://api.elevenlabs.io/v1";

    public ElevenLabsTtsProvider(String apiKey) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] synthesize(String text, VoiceParameters params) {
        try {
            // หมายเหตุ: ElevenLabs ไม่รองรับ SSML อย่างเต็มรูปแบบ
            // เราจะส่งข้อความธรรมดาและใช้พารามิเตอร์เพื่อควบคุมอารมณ์
            String cleanText = text.replaceAll("<[^>]*>", ""); // ลบแท็ก SSML

            String voiceId = params.getVoiceId();
            String url = baseUrl + "/text-to-speech/" + voiceId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("xi-api-key", apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("text", cleanText);

            Map<String, Object> voiceSettings = new HashMap<>();
            // แปลงจากพารามิเตอร์ของเราเป็นพารามิเตอร์ของ ElevenLabs
            // ElevenLabs ใช้ stability และ similarity_boost แทน pitch และ rate
            voiceSettings.put("stability", 0.5); // ค่าระหว่าง 0 ถึง 1
            voiceSettings.put("similarity_boost", 0.75); // ค่าระหว่าง 0 ถึง 1

            body.put("voice_settings", voiceSettings);

            HttpEntity<String> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error synthesizing speech with ElevenLabs: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "ElevenLabs";
    }
}
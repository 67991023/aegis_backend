package com.aegis.aiservice.service.providers;

import com.aegis.aiservice.dto.VoiceParameters;
import org.springframework.stereotype.Component;

@Component
public class MockTtsProvider implements TtsProvider {

    @Override
    public byte[] synthesize(String text, VoiceParameters params) {
        // Return dummy audio data for testing
        System.out.println("Mock TTS Provider called with text: " + text);
        return new byte[1024]; // Return dummy data
    }
}
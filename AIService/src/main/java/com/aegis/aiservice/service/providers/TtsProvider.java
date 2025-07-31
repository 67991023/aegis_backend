package com.aegis.aiservice.service.providers;

import com.aegis.aiservice.dto.VoiceParameters;

public interface TtsProvider {
    byte[] synthesize(String text, VoiceParameters params);
    String getName();
}
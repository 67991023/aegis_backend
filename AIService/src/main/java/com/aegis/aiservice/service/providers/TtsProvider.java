package com.aegis.aiservice.service.providers;

import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.service.providers.TtsProvider;

public interface TtsProvider {
    byte[] synthesize(String text, VoiceParameters params);
    String getName();
}
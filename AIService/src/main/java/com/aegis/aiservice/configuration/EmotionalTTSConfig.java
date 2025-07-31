package com.aegis.aiservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aegis.aiservice.service.providers.GoogleTtsProvider;
import com.aegis.aiservice.service.providers.ElevenLabsTtsProvider;
import com.aegis.aiservice.service.providers.AzureTtsProvider;
// Add this import
import com.aegis.aiservice.service.providers.TtsProvider;
import com.aegis.aiservice.service.providers.MockTtsProvider;

@Configuration
public class EmotionalTTSConfig {

    @Value("${tts.provider:azure}")
    private String provider;

    @Value("${tts.api-key}")
    private String apiKey;

    @Value("${tts.region:eastus}")
    private String region;

    @Bean(name = "emotionalTtsProvider")
    public TtsProvider ttsProvider() {
        return new MockTtsProvider();
    }
}
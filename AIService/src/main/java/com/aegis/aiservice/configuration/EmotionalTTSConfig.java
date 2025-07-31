package com.aegis.aiservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aegis.aiservice.service.providers.GoogleTtsProvider;
import com.aegis.aiservice.service.providers.ElevenLabsTtsProvider;
import com.aegis.aiservice.service.providers.AzureTtsProvider;
// Add this import
import com.aegis.aiservice.service.providers.TtsProvider;

@Configuration
public class EmotionalTTSConfig {

    @Value("${tts.provider:azure}")
    private String provider;

    @Value("${tts.api-key}")
    private String apiKey;

    @Value("${tts.region:eastus}")
    private String region;

    @Bean
    public TtsProvider ttsProvider() {
        switch (provider.toLowerCase()) {
            case "google":
                return new GoogleTtsProvider(apiKey);
            case "elevenlabs":
                return new ElevenLabsTtsProvider(apiKey);
            case "azure":
            default:
                return new AzureTtsProvider(apiKey, region);
        }
    }
}
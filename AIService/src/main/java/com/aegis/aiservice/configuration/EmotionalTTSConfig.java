package com.aegis.aiservice.configuration;

import com.aegis.aiservice.service.providers.TtsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmotionalTTSConfig {

    @Value("${azure.speech.subscription-key}")
    private String subscriptionKey;

    @Value("${azure.speech.region}")
    private String region;

    @Bean(name = "emotionalTtsProvider")
    public TtsProvider ttsProvider() {
        System.out.println("Initializing Azure Emotional TTS provider");
        return new AzureTtsProvider(subscriptionKey, region);
    }
}
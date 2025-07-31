package com.aegis.aiservice.configuration;

import com.aegis.aiservice.service.providers.*;
import com.aegis.aiservice.dto.VoiceParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class TtsProviderConfig {

    private static final Logger logger = LoggerFactory.getLogger(TtsProviderConfig.class);

    @Value("${tts.provider:google}")
    private String provider;

    @Value("${tts.api-key:}")
    private String apiKey;

    @Value("${tts.region:global}")
    private String region;

    @Value("${tts.default-pitch:0}")
    private float defaultPitch;

    @Value("${tts.default-rate:1.0}")
    private float defaultRate;

    @Value("${tts.default-volume:1.0}")
    private float defaultVolume;

    @Value("${tts.emotion-intensity:0.7}")
    private float emotionIntensity;

    @Value("${tts.voice-id.th-female:th-TH-PremwadeeNeural}")
    private String thFemaleVoiceId;

    @Value("${tts.voice-id.th-male:th-TH-NiwatNeural}")
    private String thMaleVoiceId;

    @Bean(name = "defaultTtsProvider")
    @Primary
    public TtsProvider ttsProvider() {
        logger.info("Initializing TTS provider: {}", provider);

        switch (provider.toLowerCase()) {
            case "azure":
                return new AzureTtsProvider(apiKey, region);
            case "elevenlabs":
                return new ElevenLabsTtsProvider(apiKey);
            case "google":
            default:
                return new GoogleTtsProvider(apiKey);
        }
    }

    public float getDefaultPitch() {
        return defaultPitch;
    }

    public float getDefaultRate() {
        return defaultRate;
    }

    public float getDefaultVolume() {
        return defaultVolume;
    }

    public float getEmotionIntensity() {
        return emotionIntensity;
    }

    public String getThFemaleVoiceId() {
        return thFemaleVoiceId;
    }

    public String getThMaleVoiceId() {
        return thMaleVoiceId;
    }
}
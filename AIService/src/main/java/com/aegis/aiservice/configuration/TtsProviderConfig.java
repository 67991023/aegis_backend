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

    @Value("${tts.provider:azure}")
    private String provider;

    @Value("${azure.speech.subscription-key:}")
    private String azureSubscriptionKey;

    @Value("${azure.speech.region:}")
    private String azureRegion;

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

    @Value("${tts.voice-id.en-female:en-US-JennyNeural}")
    private String enFemaleVoiceId;

    @Value("${tts.voice-id.en-male:en-US-GuyNeural}")
    private String enMaleVoiceId;

    @Bean(name = "defaultTtsProvider")
    @Primary
    public TtsProvider ttsProvider() {
        logger.info("Initializing TTS provider: {}", provider);

        try {
            switch (provider.toLowerCase()) {
                case "azure":
                    if (azureSubscriptionKey != null && !azureSubscriptionKey.isEmpty() &&
                            azureRegion != null && !azureRegion.isEmpty()) {
                        try {
                            return new AzureTtsProvider(azureSubscriptionKey, azureRegion);
                        } catch (Exception e) {
                            logger.error("Failed to initialize Azure TTS provider: {}", e.getMessage());
                            logger.info("Falling back to MockTtsProvider");
                            return new MockTtsProvider();
                        }
                    } else {
                        logger.error("Azure Speech configuration missing (subscription-key or region)");
                        logger.info("Falling back to MockTtsProvider");
                        return new MockTtsProvider();
                    }

                case "elevenlabs":
                    if (apiKey != null && !apiKey.isEmpty()) {
                        try {
                            return new ElevenLabsTtsProvider(apiKey);
                        } catch (Exception e) {
                            logger.error("Failed to initialize ElevenLabs TTS provider: {}", e.getMessage());
                            logger.info("Falling back to MockTtsProvider");
                            return new MockTtsProvider();
                        }
                    } else {
                        logger.error("ElevenLabs API key is missing");
                        logger.info("Falling back to MockTtsProvider");
                        return new MockTtsProvider();
                    }

                case "google":
                    try {
                        return new GoogleTtsProvider(apiKey);
                    } catch (Exception e) {
                        logger.error("Failed to initialize Google TTS provider: {}", e.getMessage());
                        logger.info("Falling back to MockTtsProvider");
                        return new MockTtsProvider();
                    }

                case "mock":
                    return new MockTtsProvider();

                default:
                    logger.warn("Unknown provider '{}', falling back to MockTtsProvider", provider);
                    return new MockTtsProvider();
            }
        } catch (Exception e) {
            logger.error("Unexpected error initializing TTS provider: {}", e.getMessage());
            return new MockTtsProvider();
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

    public String getEnFemaleVoiceId() {
        return enFemaleVoiceId;
    }

    public String getEnMaleVoiceId() {
        return enMaleVoiceId;
    }
}
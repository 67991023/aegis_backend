package com.aegis.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TtsProviderConfig {

    @Value("${tts.default-pitch:0}")
    private float defaultPitch;

    @Value("${tts.default-rate:1.0}")
    private float defaultRate;

    @Value("${tts.default-volume:1.0}")
    private float defaultVolume;

    @Value("${tts.emotion-intensity:0.7}")
    private float emotionIntensity;

    @Value("${tts.voice-id.th-female:th-TH-PremwadeeNeural}")
    private String thFemaleVoice;

    @Value("${tts.voice-id.th-male:th-TH-NiwatNeural}")
    private String thMaleVoice;

    // Getters
    public float getDefaultPitch() { return defaultPitch; }
    public float getDefaultRate() { return defaultRate; }
    public float getDefaultVolume() { return defaultVolume; }
    public float getEmotionIntensity() { return emotionIntensity; }
    public String getThFemaleVoice() { return thFemaleVoice; }
    public String getThMaleVoice() { return thMaleVoice; }
}
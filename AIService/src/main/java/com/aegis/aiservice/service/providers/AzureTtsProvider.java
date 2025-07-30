package com.aegis.aiservice.service.providers;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;
import java.io.ByteArrayOutputStream;

public class AzureTtsProvider implements TtsProvider {

    private final String apiKey;
    private final String region;

    public AzureTtsProvider(String apiKey, String region) {
        this.apiKey = apiKey;
        this.region = region;
    }

    @Override
    public byte[] synthesize(String ssml, VoiceParameters params) {
        try {
            SpeechConfig config = SpeechConfig.fromSubscription(apiKey, region);
            config.setSpeechSynthesisVoiceName(params.getVoiceId());

            // สร้างสตรีมเสียง
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            AudioOutputStream audioOutputStream = AudioOutputStream.createPullStream();

            SpeechSynthesizer synthesizer = new SpeechSynthesizer(config, audioOutputStream);
            SpeechSynthesisResult result = synthesizer.speakSsmlAsync(ssml).get();

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audio = result.getAudioData();
                return audio;
            } else {
                throw new Exception("Speech synthesis failed: " + result.getReason());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error synthesizing speech with Azure: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "Azure";
    }
}
package com.aegis.aiservice.service.providers;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;
import java.io.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aegis.aiservice.dto.VoiceParameters;

public class AzureTtsProvider implements TtsProvider {

    private static final Logger logger = LoggerFactory.getLogger(AzureTtsProvider.class);
    private final String apiKey;
    private final String region;
    private final SpeechConfig speechConfig;
    private final SpeechSynthesizer defaultSynthesizer;

    public AzureTtsProvider(String apiKey, String region) {
        this.apiKey = apiKey;
        this.region = region;

        try {
            // Create a reusable speech config
            this.speechConfig = SpeechConfig.fromSubscription(apiKey, region);
            // Setup output format to MP3
            speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz64KBitRateMonoMp3);
            // Create a default synthesizer
            this.defaultSynthesizer = new SpeechSynthesizer(speechConfig, null);
            logger.info("Azure TTS provider initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Azure TTS: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Azure TTS", e);
        }
    }

    @Override
    public byte[] synthesize(String ssml, VoiceParameters params) {
        long startTime = System.currentTimeMillis();
        SpeechSynthesizer synthesizer = null;

        try {
            // If voice is different from default, create a new synthesizer
            if (params.getVoiceId() != null && !params.getVoiceId().isEmpty()) {
                SpeechConfig config = SpeechConfig.fromSubscription(apiKey, region);
                config.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio16Khz64KBitRateMonoMp3);
                config.setSpeechSynthesisVoiceName(params.getVoiceId());
                synthesizer = new SpeechSynthesizer(config, null);
            } else {
                // Use default synthesizer
                synthesizer = defaultSynthesizer;
            }

            // Use lowercase method name as per correct API
            SpeechSynthesisResult result = synthesizer.SpeakSsmlAsync(ssml).get();

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audioData = result.getAudioData();
                long endTime = System.currentTimeMillis();
                logger.debug("Azure TTS synthesis completed in {} ms. Audio size: {} bytes",
                        (endTime - startTime), audioData.length);
                return audioData;
            } else {
                logger.error("Azure TTS synthesis failed: {}", result.getReason());
                throw new RuntimeException("Speech synthesis failed: " + result.getReason());
            }
        } catch (Exception e) {
            logger.error("Error synthesizing speech with Azure: {}", e.getMessage(), e);
            throw new RuntimeException("Error synthesizing speech with Azure: " + e.getMessage(), e);
        } finally {
            // Clean up resources if we created a new synthesizer
            if (synthesizer != null && synthesizer != defaultSynthesizer) {
                try {
                    synthesizer.close();
                } catch (Exception e) {
                    logger.warn("Error closing Azure synthesizer: {}", e.getMessage());
                }
            }
        }
    }


    @Override
    public String getName() {
        return "Azure";
    }

    public void close() {
        try {
            if (defaultSynthesizer != null) {
                defaultSynthesizer.close();
            }
            if (speechConfig != null) {
                speechConfig.close();
            }
            logger.info("Azure TTS resources closed successfully");
        } catch (Exception e) {
            logger.warn("Error closing Azure TTS resources: {}", e.getMessage());
        }
    }
}
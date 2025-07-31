package com.aegis.aiservice.service.providers;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleTtsProvider implements TtsProvider {

    private static final Logger logger = LoggerFactory.getLogger(GoogleTtsProvider.class);
    private final String apiKey;
    private final TextToSpeechClient textToSpeechClient;

    public GoogleTtsProvider(String apiKey) {
        this.apiKey = apiKey;
        try {
            // Create a single instance of the TextToSpeechClient to be reused for all requests
            this.textToSpeechClient = TextToSpeechClient.create();
            logger.info("Google TTS client initialized successfully");
        } catch (IOException e) {
            logger.error("Failed to initialize Google TTS client: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Google TTS client", e);
        }
    }

    @Override
    public byte[] synthesize(String ssml, VoiceParameters params) {
        long startTime = System.currentTimeMillis();
        try {
            // Create the synthesis input with the SSML content
            SynthesisInput input = SynthesisInput.newBuilder().setSsml(ssml).build();

            // Configure voice parameters
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("th-TH")
                    .setName(params.getVoiceId())
                    .build();

            // Configure audio output format
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(params.getSpeakingRate())
                    .setPitch(params.getPitch())
                    .setVolumeGainDb((float)params.getVolume())
                    .build();

            // Send the request to Google's TTS API
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Extract audio data from the response
            ByteString audioContents = response.getAudioContent();
            byte[] audioData = audioContents.toByteArray();

            // Log processing time for performance monitoring
            long endTime = System.currentTimeMillis();
            logger.debug("Google TTS synthesis completed in {} ms. Audio size: {} bytes",
                    (endTime - startTime), audioData.length);

            return audioData;
        } catch (Exception e) {
            logger.error("Error synthesizing speech with Google: {}", e.getMessage(), e);
            throw new RuntimeException("Error synthesizing speech with Google: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "Google";
    }

    /**
     * Clean up resources when this provider is no longer needed.
     * This should be called when the application is shutting down.
     */
    public void close() {
        if (textToSpeechClient != null) {
            try {
                textToSpeechClient.close();
                logger.info("Google TTS client closed successfully");
            } catch (Exception e) {
                logger.warn("Error closing Google TTS client: {}", e.getMessage());
            }
        }
    }
}
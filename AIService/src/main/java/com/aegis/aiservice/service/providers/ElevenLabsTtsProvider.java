package com.aegis.aiservice.service.providers;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.aegis.aiservice.dto.VoiceParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ElevenLabsTtsProvider implements TtsProvider {

    private static final Logger logger = LoggerFactory.getLogger(ElevenLabsTtsProvider.class);
    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://api.elevenlabs.io/v1";
    private final CloseableHttpClient httpClient;

    public ElevenLabsTtsProvider(String apiKey) {
        this.apiKey = apiKey;

        // Create a connection manager with connection pooling
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setValidateAfterInactivity(10000);

        // Configure timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(5000)
                .build();

        // Build the HTTP client
        this.httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .build();

        // Create a request factory using the pooled HTTP client
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setBufferRequestBody(false); // Don't buffer the entire request body in memory

        // Initialize RestTemplate with the factory
        this.restTemplate = new RestTemplate(requestFactory);
        this.objectMapper = new ObjectMapper();

        logger.info("ElevenLabs TTS provider initialized with connection pooling");
    }

    @Override
    public byte[] synthesize(String text, VoiceParameters params) {
        long startTime = System.currentTimeMillis();

        try {
            // ElevenLabs doesn't fully support SSML, so remove any tags
            String cleanText = text.replaceAll("<[^>]*>", "");
            logger.debug("Original text length: {}, cleaned text length: {}",
                    text.length(), cleanText.length());

            // Use a default voice ID if none is provided
            String voiceId = params.getVoiceId();
            if (voiceId == null || voiceId.isEmpty()) {
                // ElevenLabs default voice - you may want to change this to your preferred voice
                voiceId = "21m00Tcm4TlvDq8ikWAM";
                logger.warn("No voice ID provided, using default: {}", voiceId);
            }

            String url = baseUrl + "/text-to-speech/" + voiceId;

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("xi-api-key", apiKey);

            // Prepare request body
            Map<String, Object> body = new HashMap<>();
            body.put("text", cleanText);

            // Map our voice parameters to ElevenLabs parameters
            Map<String, Object> voiceSettings = new HashMap<>();

            // Convert our parameters to ElevenLabs parameters
            // stability: Lower values are more creative, higher values are more stable/consistent
            float stability = convertPitchToStability(params.getPitch());

            // similarity_boost: Higher values make voice more closely match reference sample
            float similarityBoost = convertRateToSimilarityBoost(params.getSpeakingRate());

            voiceSettings.put("stability", stability);
            voiceSettings.put("similarity_boost", similarityBoost);

            // Add volume control if supported by ElevenLabs
            if (params.getVolume() > 0) {
                voiceSettings.put("volume", params.getVolume());
            }

            body.put("voice_settings", voiceSettings);

            // Create the request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(
                    objectMapper.writeValueAsString(body), headers);

            logger.debug("Sending request to ElevenLabs API: {}", url);

            // Make the request to ElevenLabs
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                byte[] audioData = response.getBody();
                long endTime = System.currentTimeMillis();
                logger.debug("ElevenLabs TTS synthesis completed in {} ms. Audio size: {} bytes",
                        (endTime - startTime), audioData.length);
                return audioData;
            } else {
                logger.error("Received non-OK response from ElevenLabs: {}", response.getStatusCode());
                throw new RuntimeException("Failed to generate speech: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error synthesizing speech with ElevenLabs: {}", e.getMessage(), e);
            throw new RuntimeException("Error synthesizing speech with ElevenLabs: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "ElevenLabs";
    }

    /**
     * Convert our pitch parameter to ElevenLabs stability parameter
     * Pitch range is typically -10 to 10 in our system
     * Stability range is 0 to 1 in ElevenLabs
     */
    private float convertPitchToStability(float pitch) {
        // Default stability value if pitch is in normal range
        float defaultStability = 0.5f;

        // If pitch is available and in reasonable range
        if (pitch != 0) {
            // Convert our pitch scale to 0-1 scale
            // Higher pitch means lower stability (more variation)
            // Lower pitch means higher stability (less variation)
            return Math.max(0, Math.min(1, defaultStability - (pitch / 20)));
        }

        return defaultStability;
    }

    /**
     * Convert our speaking rate parameter to ElevenLabs similarity_boost parameter
     * Rate range is typically 0.5 to 2.0 in our system
     * Similarity_boost range is 0 to 1 in ElevenLabs
     */
    private float convertRateToSimilarityBoost(float rate) {
        // Default similarity boost
        float defaultSimilarityBoost = 0.75f;

        // If rate is available and in reasonable range
        if (rate > 0) {
            // Faster rate means higher similarity boost (more consistent)
            // Slower rate means lower similarity boost (more variation)
            return Math.max(0, Math.min(1, defaultSimilarityBoost * rate / 1.5f));
        }

        return defaultSimilarityBoost;
    }

    /**
     * Clean up resources when this provider is no longer needed
     */
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
                logger.info("ElevenLabs HTTP client closed successfully");
            } catch (Exception e) {
                logger.warn("Error closing HTTP client: {}", e.getMessage());
            }
        }
    }
}
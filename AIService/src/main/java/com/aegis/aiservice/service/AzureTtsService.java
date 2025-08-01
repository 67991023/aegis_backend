package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AzureTtsService {

    private final WebClient webClient;
    private final String subscriptionKey;
    private final String region;

    public AzureTtsService(
            @Value("${azure.speech.subscription-key}") String subscriptionKey,
            @Value("${azure.speech.region}") String region) {
        this.subscriptionKey = subscriptionKey;
        this.region = region;
        this.webClient = WebClient.builder()
                .baseUrl("https://" + region + ".tts.speech.microsoft.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/ssml+xml")
                .defaultHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .defaultHeader("X-Microsoft-OutputFormat", "audio-16khz-128kbitrate-mono-mp3")
                .build();
    }

    public Mono<Resource> convertToSpeech(String text, String voiceName) {
        String ssml = createSsml(text, voiceName);

        return webClient.post()
                .uri("/cognitiveservices/v1")
                .body(BodyInserters.fromValue(ssml))
                .retrieve()
                .bodyToMono(byte[].class)
                .map(ByteArrayResource::new);
    }

    private String createSsml(String text, String voiceName) {
        // Escape any XML special characters in the text
        String escapedText = text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");

        return "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"en-US\">" +
                "<voice name=\"" + voiceName + "\">" +
                escapedText +
                "</voice></speak>";
    }
}
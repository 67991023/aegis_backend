package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
public class BufferedAzureTtsService {

    private final WebClient webClient;

    public BufferedAzureTtsService(
            @Value("${azure.speech.subscription-key}") String subscriptionKey,
            @Value("${azure.speech.region}") String region) {

        // Increase the memory buffer size to 16MB
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();

        // Configure HTTP client with increased response size limits
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(java.time.Duration.ofSeconds(60));

        this.webClient = WebClient.builder()
                .baseUrl("https://" + region + ".tts.speech.microsoft.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
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
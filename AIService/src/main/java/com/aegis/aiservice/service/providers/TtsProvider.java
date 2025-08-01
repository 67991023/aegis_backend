package com.aegis.aiservice.service.providers;

import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

public interface TtsProvider {
    Mono<Resource> synthesizeSpeech(String text, String voice);
}
package com.aegis.aiservice.service.providers;

import com.aegis.aiservice.dto.VoiceParameters;

public interface TtsProvider {
    /**
     * Synthesize speech from text
     * @param text The text or SSML to synthesize
     * @param params Voice parameters for the synthesis
     * @return Audio data as byte array
     */
    byte[] synthesize(String text, VoiceParameters params);

    /**
     * Get the name of the provider
     * @return Provider name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}
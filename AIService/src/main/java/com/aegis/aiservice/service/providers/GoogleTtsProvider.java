package com.aegis.aiservice.service.providers;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;

public class GoogleTtsProvider implements TtsProvider {

    private final String apiKey;

    public GoogleTtsProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public byte[] synthesize(String ssml, VoiceParameters params) {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setSsml(ssml).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("th-TH")
                    .setName(params.getVoiceId())
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(params.getSpeakingRate())
                    .setPitch(params.getPitch())
                    .setVolumeGainDb((float)params.getVolume())
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            return audioContents.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error synthesizing speech with Google: " + e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "Google";
    }
}
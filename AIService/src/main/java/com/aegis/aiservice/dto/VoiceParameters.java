package com.aegis.aiservice.dto;

/**
 * Data Transfer Object containing parameters for voice synthesis
 */
public class VoiceParameters {
    private String voiceId;
    private float pitch = 0.0f;
    private float speakingRate = 1.0f;
    private float volume = 1.0f;

    public VoiceParameters() {
        // Default constructor
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getSpeakingRate() {
        return speakingRate;
    }

    public void setSpeakingRate(float speakingRate) {
        this.speakingRate = speakingRate;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "VoiceParameters{" +
                "voiceId='" + voiceId + '\'' +
                ", pitch=" + pitch +
                ", speakingRate=" + speakingRate +
                ", volume=" + volume +
                '}';
    }
}
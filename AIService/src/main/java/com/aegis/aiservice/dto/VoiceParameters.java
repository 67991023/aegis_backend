package com.aegis.aiservice.dto;

public class VoiceParameters {
    private float pitch;
    private double speakingRate;
    private double volume;
    private String voiceId;

    public VoiceParameters() {
        this.pitch = 0;
        this.speakingRate = 1.0;
        this.volume = 1.0;
    }

    // Getters and setters
    public float getPitch() { return pitch; }
    public void setPitch(float pitch) { this.pitch = pitch; }
    public double getSpeakingRate() { return speakingRate; }
    public void setSpeakingRate(double speakingRate) { this.speakingRate = speakingRate; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    public String getVoiceId() { return voiceId; }
    public void setVoiceId(String voiceId) { this.voiceId = voiceId; }
}
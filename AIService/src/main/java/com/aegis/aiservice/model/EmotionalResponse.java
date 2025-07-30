package com.aegis.aiservice.model;

import java.time.LocalDateTime;

public class EmotionalResponse {
    private EmotionType type;
    private float intensity;
    private LocalDateTime timestamp;

    public EmotionalResponse(EmotionType type, float intensity) {
        this.type = type;
        this.intensity = intensity;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public EmotionType getType() { return type; }
    public void setType(EmotionType type) { this.type = type; }
    public float getIntensity() { return intensity; }
    public void setIntensity(float intensity) { this.intensity = intensity; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
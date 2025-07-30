package com.aegis.aiservice.dto;

public class EmotionalResponseRequest {
    private String message;
    private String sessionId;
    private String userEmotion;
    private String preferredVoiceGender;

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getUserEmotion() { return userEmotion; }
    public void setUserEmotion(String userEmotion) { this.userEmotion = userEmotion; }
    public String getPreferredVoiceGender() { return preferredVoiceGender; }
    public void setPreferredVoiceGender(String preferredVoiceGender) { this.preferredVoiceGender = preferredVoiceGender; }
}
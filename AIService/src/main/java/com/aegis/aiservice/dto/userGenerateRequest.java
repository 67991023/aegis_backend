package com.aegis.aiservice.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data  // This should generate getters and setters, but we'll add them explicitly
public class userGenerateRequest {
    private String message;
    private String sessionId;

    // Explicitly add getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
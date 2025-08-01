package com.aegis.aiservice.service;

import com.aegis.aiservice.model.EmotionalResponse;
import org.springframework.stereotype.Service;

@Service
public class VoicePersonalityService {

    public String selectAppropriateVoice(EmotionalResponse emotion, String preferredGender) {
        // Default voice if no gender preference
        if (preferredGender == null || preferredGender.isEmpty()) {
            return "en-US-JennyNeural";
        }

        // Convert EmotionType to String (assuming getType() returns EmotionType)
        String emotionStr = emotion.getType() != null ? emotion.getType().toString().toLowerCase() : "neutral";

        // Select based on gender preference
        if (preferredGender.equalsIgnoreCase("male")) {
            return selectMaleVoice(emotionStr);
        } else {
            return selectFemaleVoice(emotionStr);
        }
    }

    private String selectMaleVoice(String emotion) {
        // Select male voice based on emotion
        switch (emotion.toLowerCase()) {
            case "happy":
                return "en-US-GuyNeural"; // Cheerful male voice
            case "sad":
                return "en-US-DavisNeural"; // More somber male voice
            case "angry":
                return "en-US-JasonNeural"; // Strong male voice
            case "fearful":
                return "en-US-TonyNeural"; // Softer male voice
            default:
                return "en-US-GuyNeural"; // Default male voice
        }
    }

    private String selectFemaleVoice(String emotion) {
        // Select female voice based on emotion
        switch (emotion.toLowerCase()) {
            case "happy":
                return "en-US-JennyNeural"; // Cheerful female voice
            case "sad":
                return "en-US-AmberNeural"; // More somber female voice
            case "angry":
                return "en-US-AriaNeural"; // Strong female voice
            case "fearful":
                return "en-US-SaraNeural"; // Softer female voice
            default:
                return "en-US-JennyNeural"; // Default female voice
        }
    }
}
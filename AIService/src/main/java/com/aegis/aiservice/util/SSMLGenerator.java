package com.aegis.aiservice.util;

import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.model.EmotionalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class SSMLGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SSMLGenerator.class);

    /**
     * Generates SSML with emotional expression for Azure Speech Service
     *
     * @param text The text to convert to speech
     * @param emotion The emotional response containing emotion type and intensity
     * @param voiceParams Voice parameters including voice ID and prosody settings
     * @return SSML string with emotion and prosody settings
     */
    public String generateEmotionalSSML(String text, EmotionalResponse emotion, VoiceParameters voiceParams) {
        StringBuilder ssml = new StringBuilder();
        ssml.append("<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xmlns:mstts=\"https://www.w3.org/2001/mstts\">");

        // Voice selection
        ssml.append("<voice name=\"").append(voiceParams.getVoiceId()).append("\">");

        // Style (emotion) selection
        String emotionType = getEmotionTypeAsString(emotion);
        String style = mapEmotionToAzureStyle(emotionType);
        double intensity = mapIntensityToAzureValue(emotion.getIntensity());

        ssml.append("<mstts:express-as style=\"").append(style).append("\" styledegree=\"").append(intensity).append("\">");

        // Add prosody if needed
        if (voiceParams.getPitch() != 0) {
            ssml.append("<prosody");

            ssml.append(" pitch=\"").append(voiceParams.getPitch()).append("%\"");

            ssml.append(">");
            ssml.append(escapeXml(text));
            ssml.append("</prosody>");
        } else {
            ssml.append(escapeXml(text));
        }

        ssml.append("</mstts:express-as>");
        ssml.append("</voice>");
        ssml.append("</speak>");

        return ssml.toString();
    }

    /**
     * Safely extracts emotion type as a string regardless of the return type of getType()
     */
    private String getEmotionTypeAsString(EmotionalResponse emotion) {
        if (emotion == null) {
            return "neutral";
        }

        try {
            Object type = emotion.getType();
            if (type == null) {
                return "neutral";
            }

            // If type is an enum, get its name
            if (type.getClass().isEnum()) {
                return ((Enum<?>) type).name();
            }

            // Otherwise, use toString()
            return type.toString();
        } catch (Exception e) {
            logger.warn("Error getting emotion type: {}", e.getMessage());
            return "neutral";
        }
    }

    /**
     * Maps emotion types from your model to Azure's supported styles
     */
    private String mapEmotionToAzureStyle(String emotionType) {
        if (emotionType == null) {
            return "neutral";
        }

        String normalizedEmotion = emotionType.toLowerCase();

        if (normalizedEmotion.contains("happy") || normalizedEmotion.contains("joy")) {
            return "cheerful";
        } else if (normalizedEmotion.contains("sad")) {
            return "sad";
        } else if (normalizedEmotion.contains("ang")) {
            return "angry";
        } else if (normalizedEmotion.contains("fear")) {
            return "fearful";
        } else if (normalizedEmotion.contains("surpris") || normalizedEmotion.contains("excit")) {
            return "excited";
        } else if (normalizedEmotion.contains("calm")) {
            return "gentle";
        } else {
            return "neutral";
        }
    }

    /**
     * Maps intensity from your model to Azure's style degree value
     */
    private double mapIntensityToAzureValue(float intensity) {
        // Map your intensity to Azure's style degree (0.01-2.0)
        return Math.min(2.0, Math.max(0.01, intensity / 5.0));
    }

    /**
     * Escapes special characters for XML
     */
    private String escapeXml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
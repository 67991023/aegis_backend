package com.aegis.aiservice.service;

import org.springframework.stereotype.Service;
import com.aegis.aiservice.model.EmotionalResponse;
import com.aegis.aiservice.model.EmotionType;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

@Service
public class EmotionAnalyzerService {

    private static final List<String> NEGATIVE_EMOTION_TERMS = Arrays.asList(
            "เศร้า", "กังวล", "วิตกกังวล", "เสียใจ", "ท้อแท้", "หดหู่", "โกรธ", "ผิดหวัง",
            "sad", "anxious", "worry", "depressed", "angry", "disappointed"
    );

    private static final List<String> UNCERTAINTY_TERMS = Arrays.asList(
            "ไม่แน่ใจ", "สับสน", "สงสัย", "อาจจะ", "บางที", "ไม่รู้", "ไม่เข้าใจ",
            "unsure", "confused", "uncertain", "maybe", "perhaps", "don't know"
    );

    public EmotionalResponse determineResponseEmotion(String responseText, String userEmotion) {
        // ประเมินอารมณ์จากข้อความของผู้ใช้และข้อความตอบกลับ
        if (containsNegativeEmotionTerms(userEmotion)) {
            // เลือกการตอบสนองด้วยความเห็นอกเห็นใจ
            return new EmotionalResponse(EmotionType.EMPATHY, 0.8f);
        } else if (containsUncertaintyTerms(userEmotion)) {
            // เลือกการตอบสนองที่ให้ความมั่นใจ
            return new EmotionalResponse(EmotionType.REASSURANCE, 0.7f);
        } else if (responseText.toLowerCase().contains("meditation") ||
                responseText.toLowerCase().contains("relax") ||
                responseText.contains("สมาธิ") ||
                responseText.contains("ผ่อนคลาย")) {
            return new EmotionalResponse(EmotionType.CALMING, 0.9f);
        } else if (responseText.toLowerCase().contains("great job") ||
                responseText.toLowerCase().contains("well done") ||
                responseText.contains("เก่งมาก") ||
                responseText.contains("ดีมาก")) {
            return new EmotionalResponse(EmotionType.ENCOURAGEMENT, 0.85f);
        }

        // ถ้าไม่พบรูปแบบเฉพาะ ให้ใช้การตอบสนองแบบสนับสนุนทั่วไป
        return new EmotionalResponse(EmotionType.SUPPORTIVE, 0.6f);
    }

    private boolean containsNegativeEmotionTerms(String text) {
        if (text == null) return false;
        return NEGATIVE_EMOTION_TERMS.stream().anyMatch(term ->
                Pattern.compile("\\b" + term + "\\b", Pattern.CASE_INSENSITIVE)
                        .matcher(text.toLowerCase())
                        .find());
    }

    private boolean containsUncertaintyTerms(String text) {
        if (text == null) return false;
        return UNCERTAINTY_TERMS.stream().anyMatch(term ->
                Pattern.compile("\\b" + term + "\\b", Pattern.CASE_INSENSITIVE)
                        .matcher(text.toLowerCase())
                        .find());
    }
}
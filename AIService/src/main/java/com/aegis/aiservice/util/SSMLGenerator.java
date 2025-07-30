package com.aegis.aiservice.util;

import org.springframework.stereotype.Component;
import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.model.EmotionalResponse;
import com.aegis.aiservice.model.EmotionType;

@Component
public class SSMLGenerator {

    public String generateEmotionalSSML(String text, EmotionalResponse emotion, VoiceParameters params) {
        StringBuilder ssml = new StringBuilder();
        ssml.append("<speak>");

        // ปรับตามอารมณ์
        switch (emotion.getType()) {
            case EMPATHY:
                ssml.append("<prosody rate=\"")
                        .append(params.getSpeakingRate())
                        .append("\" pitch=\"")
                        .append(params.getPitch())
                        .append("Hz\" volume=\"")
                        .append(params.getVolume())
                        .append("\">");
                ssml.append(text);
                ssml.append("</prosody>");
                break;

            case ENCOURAGEMENT:
                ssml.append("<prosody rate=\"")
                        .append(params.getSpeakingRate())
                        .append("\" pitch=\"+")
                        .append(params.getPitch())
                        .append("Hz\" volume=\"loud\">");
                ssml.append("<emphasis level=\"moderate\">")
                        .append(text)
                        .append("</emphasis>");
                ssml.append("</prosody>");
                break;

            case CALMING:
                ssml.append("<prosody rate=\"x-slow\" pitch=\"")
                        .append(params.getPitch())
                        .append("Hz\" volume=\"soft\">");
                ssml.append("<break time=\"500ms\"/>");
                ssml.append(text.replace(".", ".<break time=\"700ms\"/>"));
                ssml.append("</prosody>");
                break;

            case REASSURANCE:
                ssml.append("<prosody rate=\"")
                        .append(params.getSpeakingRate())
                        .append("\" pitch=\"")
                        .append(params.getPitch())
                        .append("Hz\">");
                ssml.append(text);
                ssml.append("</prosody>");
                break;

            default:
                ssml.append("<prosody rate=\"")
                        .append(params.getSpeakingRate())
                        .append("\" pitch=\"")
                        .append(params.getPitch())
                        .append("Hz\" volume=\"")
                        .append(params.getVolume())
                        .append("\">");
                ssml.append(text);
                ssml.append("</prosody>");
        }

        ssml.append("</speak>");
        return ssml.toString();
    }

    // เพิ่มเมธอดสำหรับกรณีฉุกเฉิน
    public String generateEmergencySSML() {
        return "<speak><prosody rate=\"slow\" pitch=\"-10%\" volume=\"loud\">" +
                "ฉันรู้สึกกังวลกับสิ่งที่คุณกำลังประสบอยู่ การพูดคุยกับผู้เชี่ยวชาญจริงๆ ในตอนนี้อาจเป็นสิ่งที่ดีที่สุด " +
                "<break time=\"300ms\"/>" +
                "คุณสามารถติดต่อสายด่วนสุขภาพจิตได้ที่หมายเลข 1323 ที่พร้อมให้ความช่วยเหลือ 24 ชั่วโมง" +
                "</prosody></speak>";
    }
}
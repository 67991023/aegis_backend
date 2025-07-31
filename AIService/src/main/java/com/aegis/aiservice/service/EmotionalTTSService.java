package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.model.EmotionalResponse;
import com.aegis.aiservice.util.SSMLGenerator;
import com.aegis.aiservice.service.providers.TtsProvider;

@Service
public class EmotionalTTSService {

    private final TtsProvider ttsProvider;
    private final EmotionAnalyzerService emotionAnalyzer;
    private final EmotionToVoiceMapper emotionMapper;
    private final VoicePersonalityService voicePersonalityService;
    private final SSMLGenerator ssmlGenerator;

    @Autowired
    public EmotionalTTSService(
            @Qualifier("emotionalTtsProvider") TtsProvider ttsProvider,
            EmotionAnalyzerService emotionAnalyzer,
            EmotionToVoiceMapper emotionMapper,
            VoicePersonalityService voicePersonalityService,
            SSMLGenerator ssmlGenerator) {
        this.ttsProvider = ttsProvider;
        this.emotionAnalyzer = emotionAnalyzer;
        this.emotionMapper = emotionMapper;
        this.voicePersonalityService = voicePersonalityService;
        this.ssmlGenerator = ssmlGenerator;
    }

    public byte[] synthesizeSpeech(String text, String detectedUserEmotion, String preferredGender) {
        // วิเคราะห์อารมณ์ที่เหมาะสมสำหรับการตอบกลับ
        EmotionalResponse emotionalResponse = emotionAnalyzer.determineResponseEmotion(text, detectedUserEmotion);

        // เลือกเสียงที่เหมาะสมตามเพศที่ผู้ใช้ต้องการและอารมณ์
        String voiceId = voicePersonalityService.selectAppropriateVoice(emotionalResponse, preferredGender);

        // แมปอารมณ์ไปยังพารามิเตอร์เสียง
        VoiceParameters voiceParams = emotionMapper.mapEmotionToVoiceParams(
                emotionalResponse.getType(),
                emotionalResponse.getIntensity()
        );
        voiceParams.setVoiceId(voiceId);

        // สร้าง SSML ที่มีพารามิเตอร์อารมณ์
        String ssml = ssmlGenerator.generateEmotionalSSML(text, emotionalResponse, voiceParams);

        // ส่งไปยัง TTS API
        return ttsProvider.synthesize(ssml, voiceParams);
    }
}
package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.configuration.TtsProviderConfig;
import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.model.EmotionType;
import com.aegis.aiservice.configuration.TtsProviderConfig;

@Service
public class EmotionToVoiceMapper {

    private final TtsProviderConfig config;

    @Autowired
    public EmotionToVoiceMapper(TtsProviderConfig config) {
        this.config = config;
    }

    public VoiceParameters mapEmotionToVoiceParams(EmotionType emotion, float intensity) {
        VoiceParameters params = new VoiceParameters();

        switch (emotion) {
            case EMPATHY:
                params.setPitch(-2 + intensity * -3); // ลดระดับเสียงเพื่อความเห็นอกเห็นใจ
                params.setSpeakingRate(1); // พูดช้าลงเล็กน้อย
                params.setVolume(1); // ลดความดังลงเล็กน้อย
                break;
            case ENCOURAGEMENT:
                params.setPitch(2 + intensity * 3); // เพิ่มระดับเสียงเพื่อความกระตือรือร้น
                params.setSpeakingRate(1); // พูดเร็วขึ้นเล็กน้อย
                params.setVolume(2); // ความดังปกติ
                break;
            case CALMING:
                params.setPitch(-3); // ระดับเสียงต่ำ
                params.setSpeakingRate(1); // พูดช้า
                params.setVolume(1); // เสียงเบา
                break;
            case REASSURANCE:
                params.setPitch(0); // ระดับเสียงปกติ
                params.setSpeakingRate(1); // พูดช้าลงเล็กน้อย
                params.setVolume(1); // เสียงเบาลงเล็กน้อย
                break;
            case SUPPORTIVE:
                params.setPitch(1); // ระดับเสียงเพิ่มขึ้นเล็กน้อย
                params.setSpeakingRate(2); // ความเร็วปกติ
                params.setVolume(2); // ความดังปกติ
                break;
            case CONCERNED:
                params.setPitch(-1); // ระดับเสียงลดลงเล็กน้อย
                params.setSpeakingRate(1); // พูดช้าลงเล็กน้อย
                params.setVolume(1); // ความดังลดลงเล็กน้อย
                break;
            case PROFESSIONAL:
                params.setPitch(0); // ระดับเสียงปกติ
                params.setSpeakingRate(1); // พูดเร็วขึ้นเล็กน้อย
                params.setVolume(1); // ความดังปกติ
                break;
            case NEUTRAL:
            default:
                params.setPitch(config.getDefaultPitch());
                params.setSpeakingRate(config.getDefaultRate());
                params.setVolume(config.getDefaultVolume());
        }

        return params;
    }
}
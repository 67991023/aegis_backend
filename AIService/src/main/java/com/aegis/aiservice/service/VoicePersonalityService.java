package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.configuration.TtsProviderConfig;
import com.aegis.aiservice.model.EmotionalResponse;
import com.aegis.aiservice.model.EmotionType;


@Service
public class VoicePersonalityService {

    private final TtsProviderConfig config;

    @Autowired
    public VoicePersonalityService(TtsProviderConfig config) {
        this.config = config;
    }

    public String selectAppropriateVoice(EmotionalResponse emotion, String preferredGender) {
        // ตรวจสอบเพศที่ต้องการ
        boolean preferFemale = !"male".equalsIgnoreCase(preferredGender);

        // เลือกเสียงตามอารมณ์และเพศที่ต้องการ
        if (preferFemale) {
            return config.getThFemaleVoiceId(); // ใช้เสียงผู้หญิงไทย
        } else {
            return config.getThMaleVoiceId(); // ใช้เสียงผู้ชายไทย
        }

        // หมายเหตุ: สำหรับการใช้งานจริง คุณอาจต้องการเพิ่มเสียงเฉพาะสำหรับอารมณ์ต่างๆ
        // เช่น เสียงผู้หญิงสำหรับอารมณ์เห็นอกเห็นใจ และเสียงผู้ชายสำหรับอารมณ์ให้กำลังใจ
    }
}
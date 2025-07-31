package com.aegis.aiservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.aegis.aiservice.service.EmotionAnalyzerService;
import com.aegis.aiservice.service.EmotionToVoiceMapper;
import com.aegis.aiservice.service.EmotionalTTSService;
import com.aegis.aiservice.service.VoicePersonalityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.aegis.aiservice.dto.VoiceParameters;
import com.aegis.aiservice.model.EmotionalResponse;
import com.aegis.aiservice.model.EmotionType;
import com.aegis.aiservice.service.providers.TtsProvider;
import com.aegis.aiservice.util.SSMLGenerator;

public class EmotionalTTSServiceTests {

    @Mock
    private TtsProvider ttsProvider;

    @Mock
    private EmotionAnalyzerService emotionAnalyzer;

    @Mock
    private EmotionToVoiceMapper emotionMapper;

    @Mock
    private VoicePersonalityService voicePersonalityService;

    @Mock
    private SSMLGenerator ssmlGenerator;

    @InjectMocks
    private EmotionalTTSService ttsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSynthesizeSpeech() {
        // Arrange
        String text = "นี่คือข้อความทดสอบ";
        String userEmotion = "sad";
        String preferredGender = "female";

        EmotionalResponse emotionalResponse = new EmotionalResponse(EmotionType.EMPATHY, 0.8f);
        when(emotionAnalyzer.determineResponseEmotion(text, userEmotion)).thenReturn(emotionalResponse);

        String voiceId = "th-TH-PremwadeeNeural";
        when(voicePersonalityService.selectAppropriateVoice(emotionalResponse, preferredGender)).thenReturn(voiceId);

        VoiceParameters voiceParams = new VoiceParameters();
        when(emotionMapper.mapEmotionToVoiceParams(emotionalResponse.getType(), emotionalResponse.getIntensity())).thenReturn(voiceParams);

        String ssml = "<speak><prosody>Test</prosody></speak>";
        when(ssmlGenerator.generateEmotionalSSML(text, emotionalResponse, voiceParams)).thenReturn(ssml);

        byte[] expectedAudio = "test audio".getBytes();
        when(ttsProvider.synthesize(ssml, voiceParams)).thenReturn(expectedAudio);

        // Act
        byte[] result = ttsService.synthesizeSpeech(text, userEmotion, preferredGender);

        // Assert
        assertArrayEquals(expectedAudio, result);
        verify(emotionAnalyzer).determineResponseEmotion(text, userEmotion);
        verify(voicePersonalityService).selectAppropriateVoice(emotionalResponse, preferredGender);
        verify(emotionMapper).mapEmotionToVoiceParams(emotionalResponse.getType(), emotionalResponse.getIntensity());
        verify(ssmlGenerator).generateEmotionalSSML(text, emotionalResponse, voiceParams);
        verify(ttsProvider).synthesize(ssml, voiceParams);
    }
}
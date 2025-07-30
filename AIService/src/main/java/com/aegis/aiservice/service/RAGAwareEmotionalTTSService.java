package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.model.EmotionType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import java.util.List;

@Service
public class RAGAwareEmotionalTTSService {

    private final EmbeddingStoreContentRetriever contentRetriever;
    private final EmotionalTTSService ttsService;

    @Autowired
    public RAGAwareEmotionalTTSService(
            EmbeddingStoreContentRetriever contentRetriever,
            EmotionalTTSService ttsService) {
        this.contentRetriever = contentRetriever;
        this.ttsService = ttsService;
    }

    public byte[] generateRAGInformedVoice(String userMessage, String sessionId, String preferredGender) {
        // ดึงข้อมูลที่เกี่ยวข้องจาก RAG
        List<TextSegment> relevantContent = contentRetriever.retrieve(userMessage);

        // วิเคราะห์อารมณ์ที่เหมาะสมจากเนื้อหาที่ดึงมา
        String detectedEmotion = analyzeEmotionFromRAGContent(relevantContent);

        // ใช้ข้อมูลจาก RAG เพื่อปรับแต่งเนื้อหาและอารมณ์ของเสียง
        String enhancedResponse = enrichResponseWithRAGContent(userMessage, relevantContent);

        // สร้างเสียงที่มีอารมณ์
        return ttsService.synthesizeSpeech(enhancedResponse, detectedEmotion, preferredGender);
    }

    private String analyzeEmotionFromRAGContent(List<TextSegment> content) {
        // วิเคราะห์เนื้อหาเพื่อกำหนดอารมณ์ที่เหมาะสม

        // ตรวจสอบเนื้อหาที่ดึงมา หากมีคำที่เกี่ยวข้องกับเทคนิคการรับมือความวิตกกังวล
        boolean hasCalmingContent = content.stream().anyMatch(segment ->
                segment.text().toLowerCase().contains("relaxation") ||
                        segment.text().toLowerCase().contains("breathing") ||
                        segment.text().contains("ผ่อนคลาย") ||
                        segment.text().contains("หายใจ")
        );

        if (hasCalmingContent) {
            return "calming";
        }

        // ตรวจสอบเนื้อหาที่ดึงมา หากมีคำที่เกี่ยวข้องกับการให้กำลังใจ
        boolean hasEncouragingContent = content.stream().anyMatch(segment ->
                segment.text().toLowerCase().contains("progress") ||
                        segment.text().toLowerCase().contains("achievement") ||
                        segment.text().contains("ก้าวหน้า") ||
                        segment.text().contains("ความสำเร็จ")
        );

        if (hasEncouragingContent) {
            return "encouraging";
        }

        return "supportive"; // ค่าเริ่มต้น
    }

    private String enrichResponseWithRAGContent(String userMessage, List<TextSegment> content) {
        // เพิ่มเนื้อหาจาก RAG เข้ากับการตอบสนอง
        StringBuilder enhancedResponse = new StringBuilder();

        // เพิ่มข้อมูลจาก RAG
        if (!content.isEmpty()) {
            // นำข้อมูล RAG มาใช้
            // คุณอาจต้องปรับโค้ดนี้ให้เข้ากับการใช้งาน RAG ของคุณ
            enhancedResponse.append("จากข้อมูลที่ฉันมี: ");
            enhancedResponse.append(content.get(0).text());
        } else {
            // ไม่มีข้อมูลเพิ่มเติมจาก RAG
            enhancedResponse.append("ฉันไม่มีข้อมูลเฉพาะเจาะจงเกี่ยวกับคำถามของคุณ แต่ฉันจะพยายามช่วยคุณตามความรู้ทั่วไป");
        }

        return enhancedResponse.toString();
    }
}
package com.aegis.aiservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aegis.aiservice.model.EmotionType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Method;

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
        // Create a Query object from the user message
        Query query = Query.from(userMessage);

        // Retrieve content objects
        List<Content> contentList = contentRetriever.retrieve(query);

        // Extract text from content using reflection or direct access
        List<String> contentTexts = extractTextsFromContent(contentList);

        // วิเคราะห์อารมณ์ที่เหมาะสมจากเนื้อหาที่ดึงมา
        String detectedEmotion = analyzeEmotionFromRAGContent(contentTexts);

        // ใช้ข้อมูลจาก RAG เพื่อปรับแต่งเนื้อหาและอารมณ์ของเสียง
        String enhancedResponse = enrichResponseWithRAGContent(userMessage, contentTexts);

        // สร้างเสียงที่มีอารมณ์
        return ttsService.synthesizeSpeech(enhancedResponse, detectedEmotion, preferredGender);
    }

    private List<String> extractTextsFromContent(List<Content> contentList) {
        // Safety check
        if (contentList == null || contentList.isEmpty()) {
            return List.of();
        }

        return contentList.stream()
                .map(content -> {
                    try {
                        // Try to access the segment - common in newer versions
                        if (hasMethod(content, "segment")) {
                            Object segment = content.getClass().getMethod("segment").invoke(content);
                            if (segment instanceof TextSegment) {
                                return ((TextSegment) segment).text();
                            }
                        }

                        // Try to access text directly - common in some versions
                        if (hasMethod(content, "text")) {
                            return (String) content.getClass().getMethod("text").invoke(content);
                        }

                        // Try to access source property - common in some versions
                        if (hasMethod(content, "source")) {
                            Object source = content.getClass().getMethod("source").invoke(content);
                            if (source instanceof String) {
                                return (String) source;
                            }
                        }

                        // Try to use toString as fallback
                        return content.toString();
                    } catch (Exception e) {
                        // Fallback to toString if all else fails
                        return content.toString();
                    }
                })
                .collect(Collectors.toList());
    }

    private boolean hasMethod(Object obj, String methodName) {
        try {
            obj.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private String analyzeEmotionFromRAGContent(List<String> contentTexts) {
        // ถ้าไม่มีเนื้อหา ให้ใช้ค่าเริ่มต้น
        if (contentTexts == null || contentTexts.isEmpty()) {
            return "supportive";
        }

        // ตรวจสอบเนื้อหาที่ดึงมา หากมีคำที่เกี่ยวข้องกับเทคนิคการรับมือความวิตกกังวล
        boolean hasCalmingContent = contentTexts.stream().anyMatch(text ->
                text.toLowerCase().contains("relaxation") ||
                        text.toLowerCase().contains("breathing") ||
                        text.contains("ผ่อนคลาย") ||
                        text.contains("หายใจ")
        );

        if (hasCalmingContent) {
            return "calming";
        }

        // ตรวจสอบเนื้อหาที่ดึงมา หากมีคำที่เกี่ยวข้องกับการให้กำลังใจ
        boolean hasEncouragingContent = contentTexts.stream().anyMatch(text ->
                text.toLowerCase().contains("progress") ||
                        text.toLowerCase().contains("achievement") ||
                        text.contains("ก้าวหน้า") ||
                        text.contains("ความสำเร็จ")
        );

        if (hasEncouragingContent) {
            return "encouraging";
        }

        return "supportive"; // ค่าเริ่มต้น
    }

    private String enrichResponseWithRAGContent(String userMessage, List<String> contentTexts) {
        // เพิ่มเนื้อหาจาก RAG เข้ากับการตอบสนอง
        StringBuilder enhancedResponse = new StringBuilder();

        // เพิ่มข้อมูลจาก RAG
        if (contentTexts != null && !contentTexts.isEmpty()) {
            // นำข้อมูล RAG มาใช้
            enhancedResponse.append("จากข้อมูลที่ฉันมี: ");
            enhancedResponse.append(contentTexts.get(0));
        } else {
            // ไม่มีข้อมูลเพิ่มเติมจาก RAG
            enhancedResponse.append("ฉันไม่มีข้อมูลเฉพาะเจาะจงเกี่ยวกับคำถามของคุณ แต่ฉันจะพยายามช่วยคุณตามความรู้ทั่วไป");
        }

        return enhancedResponse.toString();
    }
}
package com.aegis.aiservice.configuration;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import com.aegis.aiservice.dto.asisstant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.spring.restclient.SpringRestClient;
import dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilder;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import opennlp.tools.tokenize.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.testcontainers.chromadb.ChromaDBContainer;


import java.time.Duration;
import java.util.List;

import static dev.langchain4j.internal.Utils.randomUUID;

@Configuration
class modelConfiguration {
    @Value("${ollama.url}")
    String ollamaURL;

    @Value("${ollama.chatmodel}")
    String ollamaChatModel;

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public ChromaDBContainer chromaDBContainer() {
        ChromaDBContainer container = new ChromaDBContainer("chromadb/chroma:0.6.0"); // Use a specific version
        container.start();
        return container;
    }

    @Bean
    EmbeddingStore<TextSegment> embeddedStore(ChromaDBContainer chromaDBContainer) {
        return ChromaEmbeddingStore.builder()
                .baseUrl(chromaDBContainer.getEndpoint())
                .collectionName("knowledge_base")
                .build();
    }

    @Bean
    EmbeddingStoreIngestor embeddedIngestor(EmbeddingStore<TextSegment> embeddedStore, EmbeddingModel embeddingModel) {

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(new DocumentByParagraphSplitter(512, 128))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddedStore)
                .build();

        String url =
                "https://www.ucl.ac.uk/brain-sciences/sites/brain_sciences/files/cbt-id-manual.pdf";
        Document CBTDocument1 = UrlDocumentLoader.load(url, new ApachePdfBoxDocumentParser());

        ingestor.ingest(CBTDocument1);

        return ingestor;
    }

    @Bean
    ChatModelListener chatModelListener() {
        return new ChatModelListener() {

            private static final Logger log = LoggerFactory.getLogger(ChatModelListener.class);

            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                System.out.println("onRequest(): " + requestContext);
                log.info("onRequest(): {}", requestContext.chatRequest());
            }

            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                log.info("onResponse(): {}", responseContext.chatResponse());
            }

            @Override
            public void onError(ChatModelErrorContext errorContext) {
                log.info("onError(): {}", errorContext.error().getMessage());
            }
        };
    }

    @Bean
    public StreamingChatModel streamingChatModel() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory());

        SpringRestClientBuilder springRestClientBuilder = SpringRestClient.builder()
                .restClientBuilder(restClientBuilder)
                .streamingRequestExecutor(new VirtualThreadTaskExecutor());

        return OllamaStreamingChatModel.builder()
                .httpClientBuilder(springRestClientBuilder)
                .baseUrl(ollamaURL)
                .temperature(1.2)
                .modelName(ollamaChatModel)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.withMaxMessages(20);
    }

    @Bean
    public asisstant assistant(StreamingChatModel model) {
        return AiServices.builder(asisstant.class)
                .streamingChatModel(model)
//                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddedStore()))
                .build();
    }

    @Bean
    public EmbeddingStoreContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.6)
                .build();
    }

}

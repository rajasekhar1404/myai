package com.myai.config;

import com.myai.service.CustomAiService;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.*;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ModelConfig {

    @Value("${spring.ai.ollama.base-url}")
    private String llmBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String modelName;

    @Value("${chroma-url}")
    private String vectorBaseUrl;

    @Value("${chroma-collection-name}")
    private String collectionName;

    @Bean
    public OllamaStreamingChatModel languageModel() {
        return OllamaStreamingChatModel.builder()
                .modelName(modelName)
                .baseUrl(llmBaseUrl)
                .temperature(0.7)
                .timeout(Duration.ofMinutes(10))
                .topK(2)
                .topP(0.2)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new OllamaEmbeddingModel(llmBaseUrl, modelName, Duration.ofMinutes(10), 5);
    }

    @Bean
    public ChromaEmbeddingStore chromaEmbeddingStore() {
        return new ChromaEmbeddingStore(vectorBaseUrl, collectionName, Duration.ofMinutes(10));
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 10))
                .embeddingModel(embeddingModel())
                .embeddingStore(chromaEmbeddingStore())
                .build();
    }

    @Bean
    public CustomAiService aiService() {
        return AiServices.builder(CustomAiService.class)
                .streamingChatLanguageModel(languageModel())
                .contentRetriever(new EmbeddingStoreContentRetriever(chromaEmbeddingStore(), embeddingModel()))
                .chatMemory(MessageWindowChatMemory.builder().maxMessages(20).build())
                .build();
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ChromaApi chromaApi(RestTemplate restTemplate) {
        return new ChromaApi(vectorBaseUrl, restTemplate);
    }

}

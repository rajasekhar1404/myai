package com.myai.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Service
@Slf4j
public class ChatService {

    @Autowired
    private CustomAiService aiService;

    @Autowired
    private ChromaApi chromaApi;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;

    public Flux<String> getResponse(String question) {
        try {
            log.info("Question: {}", question);
            Sinks.Many<String> response = Sinks.many().replay().all();
            aiService.chat(question)
                    .onNext(x -> response.emitNext(x, Sinks.EmitFailureHandler.busyLooping(Duration.ofMinutes(1))))
                    .onComplete(re -> System.out.println(re.content()))
                    .onError(e -> System.out.println(e.getMessage()))
                    .start();
            return response.asFlux();
        } catch (Exception ex) {
            log.error("Exception while querying question: {} with message: {}", question, ex.getMessage());
            return Flux.just("Failed to query: " + question);
        }

    }

    public String getDocuments() {
        ChromaApi.Collection collection = chromaApi.getCollection("my_bot");
        Embedding embedding = embeddingModel.embed("Fountainhead").content();
        ChromaApi.QueryRequest request = new ChromaApi.QueryRequest(embedding.vectorAsList(), 10);
        ChromaApi.QueryResponse queryResponse = chromaApi.queryCollection(collection.id(), request);
        log.info("Query response: {}", queryResponse);
        return String.valueOf(queryResponse.documents());
    }

}

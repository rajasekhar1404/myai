package com.myai.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chroma.ChromaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class IngestService {

    @Autowired
    private ChromaApi chromaApi;

    @Autowired
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    public void ingestDocuments(List<Document> documents) {
        try {
            log.info("Started ingesting documents at: {}", new Date());
            embeddingStoreIngestor.ingest(documents);
            log.info("Successfully completed ingestion");
        } catch (Exception ex) {
            log.error("Exception while ingesting documents: {}", ex.getMessage());
        }
    }

    public void reset() {
        List<ChromaApi.Collection> collections = chromaApi.listCollections();
        log.info("Collections before resetting: {}", collections);
        collections.stream().map(ChromaApi.Collection::name).forEach(chromaApi::deleteCollection);
        log.info("Collections after reset: {}", chromaApi.listCollections());
        log.info("Creating collection again.");
        ChromaApi.Collection collection = chromaApi.createCollection(new ChromaApi.CreateCollectionRequest("my_bot"));
        log.info("Created collection: {}", collection.name());
    }

}

package com.ai.springai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openAIApiKey;

//    @Bean
//    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
//        return new SimpleVectorStore(embeddingModel);
//    }
//
//    @Bean
//    public EmbeddingModel embeddingModel() {
//        return new OpenAiEmbeddingModel(new OpenAiApi(openAIApiKey));
//    }
}
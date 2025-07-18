package com.ai.springai.etl.controller;

import com.ai.springai.etl.dto.ResponseDto;
import com.ai.springai.etl.service.EtlService;
import com.ai.springai.rag.Models;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/etl")
public class EtlController {

    private final EtlService etlService;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public EtlController(@Qualifier("openAiChatClient") ChatClient chatModel, EtlService etlService, VectorStore vectorStore) {
        this.etlService = etlService;
        this.chatClient = chatModel;
        this.vectorStore = vectorStore;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "saveAsMarkdown", defaultValue = "false") boolean saveAsMarkdown,
            @RequestParam(value = "saveToVectorStore", defaultValue = "true") boolean saveToVectorStore) {
        log.info("Received request to process file: {}", file.getOriginalFilename());
        try {
            etlService.processFile(file, saveAsMarkdown, saveToVectorStore);
            log.info("File processed successfully: {}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body("File processed successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error processing file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not process file: " + e.getMessage());
        }
    }

    @GetMapping("/chat")
    public ResponseDto faq(@RequestParam(value = "message", defaultValue = "Give me a list of all the models from OpenAI along with their context window in JSON format.") String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .entity(ResponseDto.class);
    }

}

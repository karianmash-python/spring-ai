package com.ai.springai.etl.controller;

import com.ai.springai.etl.service.EtlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/etl")
public class EtlController {

    private static final Logger logger = LoggerFactory.getLogger(EtlController.class);
    private final EtlService etlService;

    public EtlController(EtlService etlService) {
        this.etlService = etlService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "saveAsMarkdown", defaultValue = "false") boolean saveAsMarkdown,
                                              @RequestParam(value = "saveToVectorStore", defaultValue = "false") boolean saveToVectorStore) {
        logger.info("Received request to process file: {}", file.getOriginalFilename());
        try {
            etlService.processFile(file, saveAsMarkdown, saveToVectorStore);
            logger.info("File processed successfully: {}", file.getOriginalFilename());
            return ResponseEntity.status(HttpStatus.OK).body("File processed successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            logger.error("Error processing file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not process file: " + e.getMessage());
        }
    }

}

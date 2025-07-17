package com.ai.springai.etl.service;

import com.ai.springai.etl.factory.DocumentReaderFactory;
import com.ai.springai.etl.loader.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtlService {

    private static final Logger logger = LoggerFactory.getLogger(EtlService.class);
    private final DocumentReaderFactory documentReaderFactory;
    private final FileLoader loader;

    public EtlService(DocumentReaderFactory documentReaderFactory, FileLoader loader) {
        this.documentReaderFactory = documentReaderFactory;
        this.loader = loader;
    }

    public void processFile(MultipartFile file) throws IOException {
        logger.info("Received file: {}, content type: {}", file.getOriginalFilename(), file.getContentType());

        Resource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        DocumentReader reader = documentReaderFactory.getReader(resource);
        List<Document> documents = reader.get();
        logger.info("Successfully read {} documents from file: {}", documents.size(), file.getOriginalFilename());

        String transformedContent = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n--- END OF DOCUMENT ---\n\n"));

        String outputFilename = getOutputFilename(file.getOriginalFilename());
        loader.load(transformedContent.getBytes(), outputFilename);
    }

    private String getOutputFilename(String originalFilename) {
        if (originalFilename == null) {
            return "output.txt";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            return originalFilename.substring(0, dotIndex) + ".txt";
        }
        return originalFilename + ".txt";
    }
}

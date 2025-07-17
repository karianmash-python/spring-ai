package com.ai.springai.etl.service;

import com.ai.springai.etl.factory.DocumentReaderFactory;
import com.ai.springai.etl.loader.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EtlService {

    private static final Logger logger = LoggerFactory.getLogger(EtlService.class);
    private final DocumentReaderFactory documentReaderFactory;
    private final FileLoader loader;
    private final DeepSeekChatModel chatClient;

    public EtlService(DocumentReaderFactory documentReaderFactory, FileLoader loader, DeepSeekChatModel chatClient) {
        this.documentReaderFactory = documentReaderFactory;
        this.loader = loader;
        this.chatClient = chatClient;
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

        String content = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n\n--- END OF DOCUMENT ---\n\n"));

        PromptTemplate promptTemplate = getPromptTemplate();
        Prompt prompt = promptTemplate.create(Map.of("text_content", content));

        String transformedContent = chatClient.call(prompt).getResult().getOutput().getText();

        String outputFilename = getOutputFilename(file.getOriginalFilename());
        if (transformedContent != null) {
            loader.load(transformedContent.getBytes(), outputFilename);
        }
    }

    private static PromptTemplate getPromptTemplate() {
        String promptString = """
                Please format the following text into a clean, well-structured Markdown document.
                
                - Use appropriate Markdown syntax (e.g., `#`, `##`, `**`, `-`) for headings, lists, sections, bold text, lists and other elements.
                - DO NOT include triple backticks or commentary.
                - Output ONLY the raw markdown text, starting directly with the content.
                
                The text to format is:
                {text_content}
                """;

        return new PromptTemplate(promptString);
    }

    private String getOutputFilename(String originalFilename) {
        if (originalFilename == null) {
            return "output.md";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            return originalFilename.substring(0, dotIndex) + ".md";
        }
        return originalFilename + ".md";
    }

}

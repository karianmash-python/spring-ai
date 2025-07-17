package com.ai.springai.etl.factory;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.document.DocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DocumentReaderFactory {

    public DocumentReader getReader(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Resource must have a filename");
        }

        if (filename.toLowerCase().endsWith(".pdf")) {
            return new PagePdfDocumentReader(resource);
        }

        if (filename.toLowerCase().endsWith(".doc") || filename.toLowerCase().endsWith(".docx") || filename.toLowerCase().endsWith(".txt") || filename.toLowerCase().endsWith(".md")) {
            return new TikaDocumentReader(resource);
        }

        // Default or throw exception
        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }
}

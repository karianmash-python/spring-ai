package com.ai.springai.etl.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileLoader {

    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);
    private final Path root = Paths.get("etl_output");

    public FileLoader() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            logger.error("Could not initialize folder for upload", e);
            throw new RuntimeException("Could not initialize folder for upload");
        }
    }

    public void load(byte[] file, String filename) {
        try {
            Path destination = this.root.resolve(filename);
            Files.write(destination, file);
            logger.info("Successfully saved processed file to: {}", destination.toAbsolutePath());
        } catch (Exception e) {
            logger.error("Could not store the file. Error: {}", e.getMessage());
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }
}

package org.json.export;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.json.exception.ExportException;
import org.json.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonPostExporter implements PostExporter {

    private final ObjectWriter writer;
    private final Path outputDirectory;

    public JsonPostExporter(ObjectMapper objectMapper, Path outputDirectory) {
        this.writer = objectMapper.writerWithDefaultPrettyPrinter();
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void export(List<Post> posts) {

        if (posts == null) {
            throw new ExportException("Posts list cannot be null");
        }

        if (posts.isEmpty()) {
            System.out.println("No posts found to export.");
            return;
        }

        try {
            Files.createDirectories(outputDirectory);
        } catch (IOException e) {
            throw new ExportException(
                    "Failed to create output directory: " + outputDirectory,
                    e
            );
        }

        Set<Integer> exportedIds = new HashSet<>();

        for (Post post : posts) {

            if (post == null) {
                continue;
            }

            if (!exportedIds.add(post.id())) {
                throw new ExportException(
                        "Duplicate post ID detected: " + post.id()
                );
            }

            Path filePath = outputDirectory.resolve(post.id() + ".json");

            try {
                this.writer.writeValue(filePath.toFile(), post);

            } catch (IOException e) {
                throw new ExportException(
                        "Failed to save file: " + filePath,
                        e
                );
            }
        }
    }
}
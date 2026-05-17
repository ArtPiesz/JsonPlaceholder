
package org.json.client.export;

import org.json.exception.ExportException;
import org.json.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.export.JsonPostExporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonPostExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldHandleEmptyPostList() {

        JsonPostExporter exporter = new JsonPostExporter(
                new ObjectMapper(),
                tempDir
        );

        assertDoesNotThrow(() -> exporter.export(List.of()));
    }

    @Test
    void shouldThrowExceptionForDuplicateIds() {

        JsonPostExporter exporter = new JsonPostExporter(
                new ObjectMapper(),
                tempDir
        );

        List<Post> posts = List.of(
                new Post(1, 1, "A", "B"),
                new Post(2, 1, "C", "D")
        );

        assertThrows(
                ExportException.class,
                () -> exporter.export(posts)
        );
    }

    @Test
    void shouldSkipNullPostsAndExportValidOnes() {

        JsonPostExporter exporter = new JsonPostExporter(
                new ObjectMapper(),
                tempDir
        );

        List<Post> posts = java.util.Arrays.asList(
                new Post(1, 1, "Title", "Body"),
                null
        );

        exporter.export(posts);

        assertTrue(tempDir.resolve("1.json").toFile().exists());
    }
}
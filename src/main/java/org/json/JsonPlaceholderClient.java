package org.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class JsonPlaceholderClient {

    private static final String POSTS_URL =
            "https://jsonplaceholder.typicode.com/posts";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Post> fetchPosts() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POSTS_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "API returned status: " + response.statusCode()
                );
            }

            Post[] posts = objectMapper.readValue(
                    response.body(),
                    Post[].class
            );

            return Arrays.asList(posts);

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse posts", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        }
    }

    public void savePosts(List<Post> posts) {
        try {
            java.nio.file.Path outputDir =
                    java.nio.file.Paths.get("output");

            java.nio.file.Files.createDirectories(outputDir);

            for (Post post : posts) {
                java.nio.file.Path filePath =
                        outputDir.resolve(post.id() + ".json");

                objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValue(filePath.toFile(), post);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save posts", e);
        }
    }
}

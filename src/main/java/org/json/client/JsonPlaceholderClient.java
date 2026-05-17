
package org.json.client;

import org.json.exception.ApiException;
import org.json.model.Post;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class JsonPlaceholderClient {

    private static final int MAX_RETRIES = 3;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String postsUrl;

    public JsonPlaceholderClient(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            String postsUrl
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.postsUrl = postsUrl;
    }

    public List<Post> fetchPosts() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postsUrl))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

            try {
                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() == 200) {

                    Post[] posts = objectMapper.readValue(
                            response.body(),
                            Post[].class
                    );

                    return Arrays.asList(posts);
                }

                if (response.statusCode() >= 500 && attempt < MAX_RETRIES) {
                    continue;
                }

                throw new ApiException(
                        "Failed to fetch posts from " + postsUrl +
                                ". HTTP status: " + response.statusCode()
                );

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ApiException("Request interrupted while fetching posts", e);
            } catch (IOException e) {
                if (attempt == MAX_RETRIES) {
                    throw new ApiException("Failed after " + MAX_RETRIES + " attempts", e);
                }
                System.err.println("Attempt " + attempt + " failed: " + e.getMessage() + ". Retrying...");
                try {
                    Thread.sleep(500L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new ApiException("Interrupted during retry backoff", ie);
                }
            }
        }

        throw new ApiException("Unexpected error while fetching posts");
    }
}
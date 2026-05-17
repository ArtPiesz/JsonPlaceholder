package org.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.client.JsonPlaceholderClient;
import org.json.config.AppConfig;
import org.json.exception.ApiException;
import org.json.exception.ExportException;
import org.json.export.JsonPostExporter;
import org.json.export.PostExporter;


import java.net.http.HttpClient;
import java.nio.file.Paths;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        try {

            AppConfig config = new AppConfig();

            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(java.time.Duration.ofSeconds(5))
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonPlaceholderClient client = new JsonPlaceholderClient(
                    httpClient,
                    objectMapper,
                    config.postsUrl()
            );

            PostExporter exporter = new JsonPostExporter(
                    objectMapper,
                    Paths.get(config.outputDirectory())
            );

            var posts = client.fetchPosts();

            exporter.export(posts);

            System.out.println(
                    "Successfully exported " + posts.size() + " posts."
            );

        } catch (ApiException | ExportException e) {

            System.err.println("Application error: " + e.getMessage());

        } catch (Exception e) {

            System.err.println(
                    "Unexpected application failure: " + e.getMessage()
            );
        }
    }
}
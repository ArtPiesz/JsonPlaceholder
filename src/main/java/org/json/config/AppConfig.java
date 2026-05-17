package org.json.config;

public class AppConfig {
    private final String postsUrl;
    private final String outputDirectory;

    public AppConfig() {
        this.postsUrl = System.getenv().getOrDefault(
                "POSTS_URL",
                "https://jsonplaceholder.typicode.com/posts"
        );
        this.outputDirectory = System.getenv().getOrDefault(
                "OUTPUT_DIR",
                "output"
        );
    }

    public String postsUrl() {
        return postsUrl;
    }

    public String outputDirectory() {
        return outputDirectory;
    }
}
package org.json;

public record Post(
        int userId,
        int id,
        String title,
        String body
) {}

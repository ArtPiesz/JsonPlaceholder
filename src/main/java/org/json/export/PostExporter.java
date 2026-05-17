package org.json.export;

import org.json.model.Post;

import java.util.List;

public interface PostExporter {

    void export(List<Post> posts);
}
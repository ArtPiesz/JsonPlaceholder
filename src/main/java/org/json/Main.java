package org.json;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        JsonPlaceholderClient client = new JsonPlaceholderClient();

        List<Post> posts = client.fetchPosts();
        client.savePosts(posts);

        System.out.println(
                "Successfully exported " + posts.size() + " posts."
        );
    }
}
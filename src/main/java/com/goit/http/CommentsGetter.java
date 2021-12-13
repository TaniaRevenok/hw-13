package com.goit.http;

/*Задание 2
Дополните программу методом, который будет выводить все комментарии к последнему посту определенного пользователя
и записывать их в файл. https://jsonplaceholder.typicode.com/users/1/posts
Последним будем считать пост с наибольшим id. https://jsonplaceholder.typicode.com/posts/10/comments
Файл должен называться "user-X-post-Y-comments.json", где Х - номер пользователя, Y - номер поста.
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        CommentsGetter comments = new CommentsGetter();
        int userId = 1;
        comments.createJsonWithAllCommentsFromLastPostByUserId(userId);
    }
}

class CommentsGetter {
    HttpClient client = HttpClient.newHttpClient();

    public void createJsonWithAllCommentsFromLastPostByUserId(int userId) throws IOException, InterruptedException {
        String allPosts = getPostsByUserId(userId);
        List<String> postsIds = getAllMatches(allPosts);
        int lastPostId = getLastPostId(postsIds);
        String allComments = getAllCommentsByPostId(lastPostId);
        String jsonFilePath = "src/main/resources/" + "user-" + userId + "-post-" + lastPostId + "-comments.json";
        createJsonWithComments(allComments, jsonFilePath);
        System.out.println("JSON filepath: " + jsonFilePath);
    }

    private String getPostsByUserId(int userId) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + userId + "/posts";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private List<String> getAllMatches(String text) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\\"id\": \\d+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    private int getNumberFromString(String text) {
        String[] parts = text.split(" ");
        return Integer.parseInt(parts[1]);
    }

    private int getLastPostId(List<String> postsIds) {
        return getNumberFromString(postsIds.get(postsIds.size() - 1));
    }

    private String getAllCommentsByPostId(int postId) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void createJsonWithComments(String str, String jsonFilePath) {
        CommentJPH[] comments = createCommentsFromJson(str);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputString = gson.toJson(comments);
        try (FileWriter output = new FileWriter(jsonFilePath)) {
            output.write(outputString);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private  CommentJPH[] createCommentsFromJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, CommentJPH[].class);
    }

    class CommentJPH {
        String postId;
        int id;
        String name;
        String email;
        String body;

        public CommentJPH(String postId, int id, String name, String email, String body) {
            this.postId = postId;
            this.id = id;
            this.name = name;
            this.email = email;
            this.body = body;
        }

        @Override
        public String toString() {
            return "CommentJPH{" +
                    "postId='" + postId + '\'' +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", body='" + body + '\'' +
                    '}';
        }
    }

}

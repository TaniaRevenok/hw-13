package com.goit.http;

/*Задание 1
Программа должна содержать методы для реализации следующего функционала:
 - создание нового объекта в https://jsonplaceholder.typicode.com/users. Возможно, вы не увидите обновлений на сайте.
   Метод создания работает правильно, если в ответ на JSON с объектом вернулся такой же JSON, но с полем id
   со значением на 1 больше, чем самый большой id на сайте.
 - обновление объекта в https://jsonplaceholder.typicode.com/users. Возможно, обновления не будут видны на сайте
   напрямую. Будем считать, что метод работает правильно, если в ответ на запрос придет обновленный JSON
   (он должен быть точно таким же, какой вы отправляли).
 - удаление объекта из https://jsonplaceholder.typicode.com/users. Здесь будем считать корректным результат -
   статус из группы 2хх в ответ на запрос.
 - получение информации обо всех пользователях https://jsonplaceholder.typicode.com/users
 - получение информации о пользователе с определенным id https://jsonplaceholder.typicode.com/users/{id}
 - получение информации о пользователе с определенным username -
   https://jsonplaceholder.typicode.com/users?username={username}
*/

/*Hints from https://jsonplaceholder.typicode.com
Getting a resource: https://jsonplaceholder.typicode.com/posts/1
Listing all resources: https://jsonplaceholder.typicode.com/posts
Creating a resource: https://jsonplaceholder.typicode.com/posts
                    headers: 'Content-type': 'application/json; charset=UTF-8'
                    method: 'POST'
                    body: JSON
                            title: 'foo',
                            body: 'bar',
                            userId: 1,
Updating a resource: https://jsonplaceholder.typicode.com/posts/1
                    headers: 'Content-type': 'application/json; charset=UTF-8'
                    method: 'PUT'
                    body: JSON
                            id: 1,
                            title: 'foo',
                            body: 'bar',
                            userId: 1,
Patching a resource: https://jsonplaceholder.typicode.com/posts/1
                    headers: 'Content-type': 'application/json; charset=UTF-8'
                    method: 'PATCH'
                    body: JSON
                            title: 'foo',
Deleting a resource: https://jsonplaceholder.typicode.com/posts/1
                    method: 'DELETE'
Filtering resources: https://jsonplaceholder.typicode.com/posts?userId=1
                    // This will return all the posts that belong to the first user
Listing nested resources: https://jsonplaceholder.typicode.com/posts/1/comments
                    // This is equivalent to /comments?postId=1
*/

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String jsonUser = "src/main/resources/user_from_JPH.json";
        String jsonNewUser = "src/main/resources/new_user_JPH.json";
        String jsonAllUsers = "src/main/resources/all_users_from_JPH.json";

        MyJsonPlaceholder myJPh = new MyJsonPlaceholder();

        int userId = 1;
        String userN1 = myJPh.getUserById(userId);
        System.out.println("User" + userId + ":\n" + userN1);
        myJPh.createJsonWithUser(userN1, jsonUser);

        String userName = "Bret";
        String userBret = myJPh.getUserByName(userName);
        System.out.println("User with name \"" + userName + ":\n" + userBret);

        System.out.println("List with all users:\n" + myJPh.getAllUsers());
        myJPh.createJsonWithUsers(myJPh.getAllUsers(), jsonAllUsers);

        myJPh.createJsonWithUser(myJPh.createUser(jsonNewUser), jsonUser);

        myJPh.createJsonWithUser(myJPh.updateUserById(userId, jsonNewUser), jsonUser);

        System.out.println("User with id=" + userId + " delete status - " + myJPh.deleteUserById(userId));
    }
}

public class MyJsonPlaceholder {

    HttpClient client = HttpClient.newHttpClient();

    public String createUser(String jsonUserFilePath) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofFile(Paths.get(jsonUserFilePath)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String updateUserById(int userId, String jsonUserFilePath) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + userId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofFile(Paths.get(jsonUserFilePath)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public int deleteUserById(int id) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public String getAllUsers() throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getUserById(int id) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getUserByName(String userName) throws IOException, InterruptedException {
        String uri = "https://jsonplaceholder.typicode.com/users?username=" + userName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public void createJsonWithUser(String jsonUser, String jsonFilePath) {
        UserJPH user = createUserFromJson(jsonUser);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputString = gson.toJson(user);
        try (FileWriter output = new FileWriter(jsonFilePath)) {
            output.write(outputString);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void createJsonWithUsers(String jsonUsers, String jsonFilePath) {
        UserJPH[] users = createUsersFromJson(jsonUsers);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputString = gson.toJson(users);
        try (FileWriter output = new FileWriter(jsonFilePath)) {
            output.write(outputString);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public UserJPH createUserFromJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, UserJPH.class);
    }

    public UserJPH[] createUsersFromJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, UserJPH[].class);
    }

    class UserJPH {
        private final int id;
        private String name;
        private String username;
        private String email;
        private Object address;
        private String phone;
        private String website;
        private Object company;

        public UserJPH(int id, String name, String username, String email, Object address, String phone, String website, Object company) {
            this.id = id;
            this.name = name;
            this.username = username;
            this.email = email;
            this.address = address;
            this.phone = phone;
            this.website = website;
            this.company = company;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        @Override
        public String toString() {
            return "UserJPH{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", username='" + username + '\'' +
                    ", email=" + email +
                    ", address=" + address +
                    ", phone='" + phone + '\'' +
                    ", website='" + website + '\'' +
                    ", company=" + company +
                    '}';
        }
    }

}

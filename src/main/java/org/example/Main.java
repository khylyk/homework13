package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* Використовую Apache HTTPClient для взаємодії з API
(це поки що найскладніша тема, написала код тільки завдяки схожим програмам з форумів
думаю, що ще далеко не все зрозуміла, що написала)*/
public class Main {

    private static final String url = "https://jsonplaceholder.typicode.com";
    private CloseableHttpClient client = HttpClientBuilder.create().build();

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        JSONObject user = new JSONObject();
        user.put("name", "boris");
        user.put("username", "borisi");
        user.put("email", "borisi@example.com");
        JSONObject createdUser = main.createUser(user);
        System.out.println("Створений користувач: " + createdUser.toString());

        List<JSONObject> userGet = main.getAllUsers();
        System.out.println("Усі користувачі: " + userGet.toString());


        JSONArray todos = main.getToDos(3);
        System.out.println("Відкриті завдання " + todos.toString());

    }

    public JSONObject createUser(JSONObject user) throws IOException {
        //створюємо та встановлюємо параметри для httpPost
        HttpPost post = new HttpPost(url + "/users");
        StringEntity entity = new StringEntity(user.toString());
        post.setEntity(entity);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        //отримуємо відповідь серверу
        HttpResponse response = client.execute(post);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = responseEntity != null ?
                new Scanner(responseEntity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        //створюємо новий JSON об'єкт
        JSONObject responseJson = new JSONObject(responseBody);
        //створюємо новий ID
        int newId = responseJson.getInt("id") + 1;
        user.put("id", newId);
        return user;
    }

    public JSONObject updateUser(JSONObject user) throws IOException {
        HttpPut put = new HttpPut(url + "/users/" + user.getInt("id"));
        StringEntity entity = new StringEntity(user.toString());
        put.setEntity(entity);
        put.setHeader("Accept", "application/json");
        put.setHeader("Content-type", "application/json");
        HttpResponse response = client.execute(put);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = responseEntity != null ?
                new Scanner(responseEntity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        //створюємо новий JSON об'єкт
        JSONObject responseJson = new JSONObject(responseBody);
        return responseJson;
    }

    public int deleteUser(int userId) throws IOException {
        HttpDelete delete = new HttpDelete(url + "/users/" + userId);
        HttpResponse response = client.execute(delete);
        return response.getStatusLine().getStatusCode();
    }

public List<JSONObject> getAllUsers() throws IOException {
    HttpGet request = new HttpGet(url + "/users");
    request.setHeader("Accept", "application/json");
    List<JSONObject> users = new ArrayList<>();
    try (CloseableHttpResponse response = client.execute(request)) {
        if (response.getStatusLine().getStatusCode() == 200) { //200 = successful request
            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            JSONArray jsonArray = new JSONArray(responseBody);
            //усіх користувачів додаємо у список
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                users.add(jsonObject);
            }
        } else {
            throw new RuntimeException("Failed to get all users: " + response.getStatusLine().getStatusCode());
        }
    }
    return users;
}


    public JSONObject getUserById(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String responseBody = entity != null ?
                new Scanner(entity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        JSONObject responseJson = new JSONObject(responseBody);
        return responseJson;
    }

    public JSONObject getUserByUsername(String username) throws IOException {
        HttpGet get = new HttpGet(url + "/users?username=" + username);
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String responseBody = entity != null ?
                new Scanner(entity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        JSONObject responseJson = new JSONObject(responseBody);
        return responseJson;
    }

    //наступні 3 методи відповідають за збереження коментарів до файлу
    public JSONObject getPost(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId + "/posts");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = responseEntity != null ?
                new Scanner(responseEntity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        JSONArray posts = new JSONArray(responseBody);
        return posts.getJSONObject(posts.length() - 1);
    }

    public JSONArray getComments(int postNumber) throws IOException {
        HttpGet get = new HttpGet(url + "/posts/" + postNumber + "/comments");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = responseEntity != null ?
                new Scanner(responseEntity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        JSONArray comments = new JSONArray(responseBody);
        return comments;
    }

    public void saveComments(int userId, int postNumber, JSONArray comments) throws IOException {
        String filename = "user-" + userId + "post-" + postNumber + "-comments.json";
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        writer.write(comments.toString());
        writer.close();

    }

    public JSONArray getToDos(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId + "/todos?completed=false");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseBody = responseEntity != null ?
                new Scanner(responseEntity.getContent(), StandardCharsets.UTF_8.name()).useDelimiter("\\Z").next() : "";
        JSONArray todos = new JSONArray("[" + responseBody + "]");
        return todos;
    }
}
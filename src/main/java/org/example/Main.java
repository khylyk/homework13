package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static final String url = "https://jsonplaceholder.typicode.com";
    private final CloseableHttpClient client = HttpClientBuilder.create().build();

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        Geo geo = new Geo("3574", "45765");
        Address address = new Address("ahmatovoy", "a", "Berlin", "85847", geo);
        Company company = new Company("BorInc", "for every fish there is a bigger fish", "fish delivery");
        User user = new User(12, "boris", "bor", "bor@gm.com", "96348", "bor.com", address, company);
        User createdUser = main.createUser(user);
        System.out.println("New user " + createdUser);
        int successsCode = main.deleteUser(user);
        System.out.println("Delete code " + successsCode);
        List<Comment> comments = main.getComments(1);
        main.saveComments(2, 1, comments);
        List<Todo> todos = main.getToDos(1);
        for(Todo todo : todos){
            System.out.println(todo);
        }

    }
    public User createUser(User user) throws IOException {
        HttpPost post = new HttpPost(url + "/users");
        Gson gson = new Gson();
        String userGson = gson.toJson(user);
        StringEntity requestEntity = new StringEntity(userGson, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);
        HttpResponse response = client.execute(post);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        return gson.fromJson(responseJson, User.class);
    }

    public User updateUser(User user) throws IOException {
        HttpPut put = new HttpPut(url + "/users/" + user.getId());
        Gson gson = new Gson();
        String userGson = gson.toJson(user);
        StringEntity requestEntity = new StringEntity(userGson, "application/json");
        put.setEntity(requestEntity);
        HttpResponse response = client.execute(put);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        return gson.fromJson(responseJson, User.class);
    }

    public int deleteUser(User user) throws IOException {
        HttpDelete delete = new HttpDelete(url + "/users/" + user.getId());
        HttpResponse response = client.execute(delete);
        return response.getStatusLine().getStatusCode();
    }

    public List<User> getUsers() throws IOException {
        HttpGet get = new HttpGet(url + "/users");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        UsersRequest users = gson.fromJson(responseJson, UsersRequest.class);
        return users.getUsers();
    }

    public User getUserById(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId);
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        return gson.fromJson(responseJson, User.class);
    }

    public User getUserByUsername(String username) throws IOException {
        HttpGet get = new HttpGet(url + "/users?username=" + username);
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        return gson.fromJson(responseJson, User.class);
    }

    private List<Post> getPosts(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId + "/posts");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();

        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        return gson.fromJson(responseJson, new TypeToken<List<Post>>(){}.getType());
    }

    private List<Comment> getComments(int postId) throws IOException {
        HttpGet get = new HttpGet(url + "/posts/" + postId + "/comments");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        return gson.fromJson(responseJson, new TypeToken<List<Post>>(){}.getType());
    }

    public void saveComments(int userId, int postId, List<Comment> comments) throws IOException {
        String filename = "user-" + userId + "post-" + postId + "-comments.json";
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        Gson gson = new Gson();
        String commentsJson = gson.toJson(comments);
        writer.write(commentsJson);
        writer.close();

    }

    public List<Todo> getToDos(int userId) throws IOException {
        HttpGet get = new HttpGet(url + "/users/" + userId + "/todos?completed=false");
        HttpResponse response = client.execute(get);
        HttpEntity responseEntity = response.getEntity();
        String responseJson = EntityUtils.toString(responseEntity);
        Gson gson = new Gson();
        return gson.fromJson(responseJson, new TypeToken<List<Todo>>(){}.getType());
    }


}



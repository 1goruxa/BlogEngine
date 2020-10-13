package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.Post;

import java.util.ArrayList;

public class PostsResponse {

    @JsonProperty("count")
    private int count;

    @JsonProperty("posts")
    private ArrayList<PostResponse> posts;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<PostResponse> posts) {
        this.posts = posts;
    }
}

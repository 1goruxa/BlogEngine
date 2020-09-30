package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.Posts;

import java.util.ArrayList;

public class PostResponse {

    @JsonProperty("count")
    private int count;

    @JsonProperty("posts")
    private ArrayList<Posts> postArrayList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Posts> getPostArrayList() {
        return postArrayList;
    }

    public void setPostArrayList(ArrayList<Posts> postArrayList) {
        this.postArrayList = postArrayList;
    }


}

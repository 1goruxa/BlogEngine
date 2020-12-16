package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public class TagsResponse {
    @JsonProperty("tags")
    private ArrayList<TagResponse> tags;


    public ArrayList<TagResponse> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagResponse> tags) {
        this.tags = tags;
    }
}

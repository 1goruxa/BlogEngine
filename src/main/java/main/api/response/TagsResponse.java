package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.Tag;

import java.util.ArrayList;


public class TagsResponse {
    @JsonProperty("tags")
    private ArrayList<Tag> tags;

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }
}

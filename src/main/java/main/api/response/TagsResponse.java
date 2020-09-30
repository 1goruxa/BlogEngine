package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.model.Tags;
import java.util.ArrayList;


public class TagsResponse {
    @JsonProperty("tags")
    private ArrayList<Tags> tags;

    public ArrayList<Tags> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tags> tags) {
        this.tags = tags;
    }
}

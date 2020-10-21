package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.Repo.TagRepository;
import main.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;

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

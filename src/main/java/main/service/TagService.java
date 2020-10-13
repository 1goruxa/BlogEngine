package main.service;

import main.api.response.TagsResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TagService {
    public TagsResponse getTags(){
        TagsResponse tagsResponse = new TagsResponse();
        tagsResponse.setTags(new ArrayList<>());

        return tagsResponse;
    }
}

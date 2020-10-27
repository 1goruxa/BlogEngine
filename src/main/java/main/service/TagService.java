package main.service;

import main.Repo.PostRepository;
import main.Repo.Tag2PostRepository;
import main.Repo.TagRepository;
import main.api.response.TagResponse;
import main.api.response.TagsResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private Tag2PostRepository tag2PostRepository;
    @Autowired
    private PostRepository postRepository;

    public TagsResponse getTags(String query) {
        TagsResponse tagsResponse = new TagsResponse();
        ArrayList<TagResponse> tagResponses = new ArrayList<>();

        //Соберем все теги
        double postCounter = postRepository.countAllByIsActiveAndModerationStatusAndTimeLessThan(1, "ACCEPTED",new Date());
        //Сортировка Tag по query из запроса (не реализовано в текущей версии фронта)
        List<Tag> tagList;
        if (query.length()!=0) {
           tagList = tagRepository.findAllByQuery(query);
        }
        else {
            tagList = tagRepository.findAll();
        }

        //Преобразуем в необходимый для апи формат
        for (Tag t : tagList) {
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(t.getText());
            double weight = 0;
            double tagCounter = tag2PostRepository.countTagsById(t.getId());

            // Считаем вес
            if (postCounter != 0) {
                weight = tagCounter / postCounter;
                if (weight < 0.1) {weight = 0.1;}
                if (weight >= 0.25 && weight < 0.5) {weight = 0.5;}
                if (weight >= 0.5) { weight = 1;}

            }
            tagResponse.setWeight(weight);
            tagResponses.add(tagResponse);
        }
        tagsResponse.setTags(tagResponses);

        return tagsResponse;
    }
}

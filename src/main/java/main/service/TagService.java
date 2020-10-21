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
        Iterable<Post> postIterable = postRepository.findAll();
        double postCounter = 0;
        for (Post p : postIterable) {
            postCounter++;
        }

        // Сортировка Tag по query из запроса (не реализовано в текущей версии фронта)
        Iterable<Tag> tagIterable;
        if (query.length()==0) {
           tagIterable = tagRepository.findAll();
        }
        else {
            tagIterable = tagRepository.findAll(QTag.tag.text.contains(query));
        }

        //Преобразуем в необходимый для апи формат
        for (Tag t : tagIterable) {

            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(t.getText());
            double weight = 0;
            double tagCounter = 0;
            Iterable<Tag2Post> tag2PostIterable = tag2PostRepository.findAll(QTag2Post.tag2Post.tag.id.eq(t.getId()));
            for (Tag2Post t2p : tag2PostIterable) {
                tagCounter++;
            }
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

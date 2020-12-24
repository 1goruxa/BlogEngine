package main.service;

import main.repo.PostRepository;
import main.repo.Tag2PostRepository;
import main.repo.TagRepository;
import main.api.response.TagResponse;
import main.api.response.TagsResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

        //Считаем вес
        //Запишем все теги и вычислим их долю в общей массе постов
        Map<Tag, Double> mapForWieghtCalc = new HashMap<>();
        for (Tag t : tagList) {
            double partOfAllPosts = 0;
            double tagCounter = tag2PostRepository.countTagsById(t.getId());
            if (postCounter != 0) {
                partOfAllPosts= tagCounter / postCounter;
                mapForWieghtCalc.put(t, partOfAllPosts);
            }
        }

        //Сортируем по весу список тегов в обратном порядке
        Map<Tag,Double> sortedTagList = sortTagMap(mapForWieghtCalc);

        //Вес распределяем по ABC a - max значения, b - средние значения, с - остальные. a+b ~ 20%, c ~ 80%
        double abcPart = 0;
        double weight;
        double maxPart = 0;
        double midPart = 0;

        //Вычисляем вес
        for (Tag t : sortedTagList.keySet()) {
            abcPart += sortedTagList.get(t);
            if (maxPart == 0){
                maxPart = sortedTagList.get(t);
                weight = 1;
            }
            else{
                if (maxPart !=0 && maxPart == sortedTagList.get(t)){
                    weight = 1;
                }
                else{
                    if (maxPart !=0 && midPart == 0){
                        midPart = sortedTagList.get(t);
                        weight = 0.5;
                    }
                    else{
                        if (maxPart !=0 && midPart != 0 && abcPart <= 0.2){
                            weight = 0.5;
                        }
                        else{
                            if (midPart == sortedTagList.get(t)){
                                weight = 0.5;
                            }
                            else{
                                weight = 0.1;
                            }
                        }
                    }
                }
            }


            //Преобразуем в необходимый для апи формат
            TagResponse tagResponse = new TagResponse();
            tagResponse.setName(t.getText());
            tagResponse.setWeight(weight);
            tagResponses.add(tagResponse);
        }
        tagsResponse.setTags(tagResponses);

        return tagsResponse;
    }

    //Сортировка Map по значениям
    private Map<Tag,Double> sortTagMap(Map<Tag,Double> unsortedMap){
        Map<Tag, Double> sortedMap = unsortedMap.entrySet().stream()
                        .sorted(Map.Entry.<Tag,Double>comparingByValue().reversed())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        return sortedMap;
    }
}

package main.service;

import main.repo.PostRepository;
import main.api.response.CalendarResponse;
import main.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CalendarService {
    @Autowired
    private PostRepository postRepository;

    public CalendarResponse getPostsByYear(int year){
        CalendarResponse calendarResponse = new CalendarResponse();
        //Добавляем список уникальных годов
        List<Integer> uniqueYearsList = postRepository.getUniqueYearsOfPosts();
        calendarResponse.setYears(uniqueYearsList);

        //Формируем список постов для year или для текущего
        HashMap<String, Integer> yearPostsList = new HashMap<>();
        List<Post> postsByYear = postRepository.getPostForYear(year);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Post p : postsByYear){
            Date postDate = p.getTime();
            if(!yearPostsList.keySet().contains(simpleDateFormat.format(postDate))){
                yearPostsList.put(simpleDateFormat.format(postDate),1);
            }
            else{
                yearPostsList.put(simpleDateFormat.format(postDate), yearPostsList.get(simpleDateFormat.format(postDate))+1);
            }

        }

        calendarResponse.setPosts(yearPostsList);

        return  calendarResponse;
    }
}
//    {
//	"years": [2017, 2018, 2019, 2020],
//	"posts": {
//		"2019-12-17": 56,
//		"2019-12-14": 11,
//		"2019-06-17": 1,
//		"2020-03-12": 6
//	}
//}
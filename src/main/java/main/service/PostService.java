package main.service;

import main.api.response.PostResponse;
import main.model.Posts;
import main.model.PostsRepository;
import main.model.QPosts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.function.Predicate;

@Service
public class PostService {
    @Autowired
    private PostsRepository postsRepository;

    public int inactivePostsCounter(){ //Посчитаем неактивные посты
        int counter = (int) postsRepository.count();
        Iterable<Posts> postsIterable = postsRepository.findAll();
        for (Posts post : postsIterable){
            if (post.getIsActive()==0) {
                counter--;
            }
        }
        return counter;
    }

    public PostResponse showPosts(int offset, int limit, String mode){

        PostResponse postResponse = new PostResponse();
        PageRequest pageReq = null;

        if (mode.equals("recent")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");
        }
        if (mode.equals("early")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        }

//        утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации не позднее текущего момента
//        (движок должен позволять откладывать публикацию).
//        popular - сортировать по убыванию количества комментариев
//        best - сортировать по убыванию количества лайков


        Page<Posts> pagedPosts = postsRepository.findAll(QPosts.posts.isActive.eq(1),pageReq);
        ArrayList<Posts> postsArrayList = new ArrayList<>();
        for (Posts post : pagedPosts){
            postsArrayList.add(post);
        }
        postResponse.setPostArrayList(postsArrayList);
        postResponse.setCount(inactivePostsCounter());

        return postResponse;
    }
}

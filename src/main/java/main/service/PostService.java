package main.service;

import main.Repo.PostRepository;
import main.api.response.PostResponse;
import main.api.response.PostsResponse;
import main.api.response.UserPostResponse;
import main.model.Post;
import main.model.QPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    //Посчитаем неактивные посты
    public int inactivePostsCounter(){
        int counter = (int) postRepository.count();
        Iterable<Post> postsIterable = postRepository.findAll();
        for (Post post : postsIterable){
            if (post.getIsActive()==0) {
                counter--;
            }
        }
        return counter;
    }

    //Возврат поста по ID
    public ResponseEntity<Post> getPostById(int id){
        Optional<Post> optionalPosts = postRepository.findById(id);
        if (optionalPosts.isPresent()){
            return new ResponseEntity(optionalPosts.get(), HttpStatus.OK);
            //!Создать новый объект, подтягивающий все поля для правильного ответа по апи
        }
        else return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    //Получаем список найденный по запросу постов
    public PostsResponse searchAndShowPosts(int offset, int limit, String query){
        PostsResponse postsResponse = new PostsResponse();
        //Вывод поиска по свежим постам
        PageRequest pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");;

        //У запроса обрежем пробелы
        query.trim();
        query = query.toLowerCase();

        Page<Post> pagedPosts = postRepository.findAll(QPost.post.text.toLowerCase().contains(query),pageReq);

        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        int localInActiveCounter = 0;
        for (Post post : pagedPosts){
            PostResponse postResponse = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            //Преобразовать post в postresponse
            postResponse.setId(post.getId());
            postResponse.setIsActive(post.getIsActive());
            postResponse.setModeratorId(post.getModeratorId());
            //!Здесь записываем анонс без тегов и с обрезкой (пока просто записываем весь текст)
            postResponse.setAnnounce(post.getText());
            postResponse.setTimestamp(post.getTime().getTime()/1000);

            postResponse.setTitle(post.getTitle());
            postResponse.setViewCount(post.getViewCount());
            userPostResponse.setName(post.getUser().getName());
            userPostResponse.setId(post.getUser().getId());
            postResponse.setUser(userPostResponse);

            if (post.getIsActive()==1){
                postArrayList.add(postResponse);
                localInActiveCounter++;
            }
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(localInActiveCounter);

        return postsResponse;

    }

    //Получаем список постов в соответсвии с режим отображения mode
    public PostsResponse showPosts(int offset, int limit, String mode){

        PostsResponse postsResponse = new PostsResponse();
        PageRequest pageReq = null;

        if (mode.equals("recent")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");
        }
        else if (mode.equals("early")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        }
        else if (mode.equals("best")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"commentsSet");
            // !       best - сортировать по убыванию количества лайков
        }
        else if (mode.equals("popular")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"votesSet");
            // !       popular - сортировать по убыванию количества комментариев
        }
        else{
            //!Тут реализовать возврат при альтернативном mode и pagreq=null, пусть грузит по дефолту шаблон recent
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");
        }

//        утверждённые модератором (поле moderation_status равно ACCEPTED) посты с датой публикации не позднее текущего момента
//        (движок должен позволять откладывать публикацию).

        Page<Post> pagedPosts = postRepository.findAll(QPost.post.isActive.eq(1),pageReq);

        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            PostResponse postResponse = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            //Преобразовать post в postresponse
            postResponse.setId(post.getId());
            postResponse.setIsActive(post.getIsActive());
            postResponse.setModeratorId(post.getModeratorId());
            //!Здесь записываем анонс без тегов и с обрезкой (пока просто записываем весь текст)
            postResponse.setAnnounce(post.getText());
            postResponse.setTimestamp(post.getTime().getTime()/1000);

            postResponse.setTitle(post.getTitle());
            postResponse.setViewCount(post.getViewCount());
            userPostResponse.setName(post.getUser().getName());
            userPostResponse.setId(post.getUser().getId());
            postResponse.setUser(userPostResponse);
            postArrayList.add(postResponse);
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(inactivePostsCounter());

        return postsResponse;

    }
}

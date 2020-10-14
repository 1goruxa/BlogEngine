package main.service;

import main.Repo.CommentRepository;
import main.Repo.PostRepository;
import main.Repo.TagRepository;
import main.api.response.*;
import main.model.*;
import org.jsoup.Jsoup;
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
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TagRepository tagRepository;
    int announceLimit = 150;

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
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        UserPostResponse userPostResponse = new UserPostResponse();
        CommentPostResponse commentPostResponse = new CommentPostResponse();
        UserCommentResponse userCommentResponse = new UserCommentResponse();
        if (optionalPosts.isPresent()){
            postByIdResponse.setId(optionalPosts.get().getId());
            postByIdResponse.setTimestamp(optionalPosts.get().getTime().getTime()/1000);

            if (optionalPosts.get().getIsActive()==1){
                postByIdResponse.setActive(true);
            }
            else{
                postByIdResponse.setActive(false);
            }

            userPostResponse.setId(optionalPosts.get().getUser().getId());
            userPostResponse.setName(optionalPosts.get().getUser().getName());
            postByIdResponse.setUser(userPostResponse);

            postByIdResponse.setTitle(optionalPosts.get().getTitle());
            postByIdResponse.setText(optionalPosts.get().getText());

            postByIdResponse.setViewCount(optionalPosts.get().getViewCount());

            //Ищем и добавляем комменты
            Iterable<Comment> commentIterable = commentRepository.findAll(QComment.comment.post.id.eq(optionalPosts.get().getId()));
            ArrayList<CommentPostResponse> commentPostResponseArrayList = new ArrayList<>();
            //преобразовать в CommentPostResponse
            for(Comment c : commentIterable){
                CommentPostResponse cpResponse = new CommentPostResponse();
                UserCommentResponse ucResponse = new UserCommentResponse();
                cpResponse.setId(c.getId());
                cpResponse.setText(c.getText());
                cpResponse.setTimestamp(c.getTime().getTime()/1000);

                ucResponse.setId(c.getUser().getId());
                ucResponse.setName(c.getUser().getName());
                ucResponse.setPhoto(c.getUser().getPhoto());

                cpResponse.setUser(ucResponse);
                commentPostResponseArrayList.add(cpResponse);
            }
            postByIdResponse.setComments(commentPostResponseArrayList);

            //! Лайки и дизлайки доделать, тэги
            postByIdResponse.setLikeCount(1);
            postByIdResponse.setDislikeCount(1);
            ArrayList<String> tmpArray = new ArrayList<>();
            tmpArray.add("TMP_TAG");
            postByIdResponse.setTags(tmpArray);


            return new ResponseEntity(postByIdResponse, HttpStatus.OK);

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

        //Ищем поиском в тексте
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
            //Анонс
            String announce = Jsoup.parse(post.getText()).text();
            if (announce.length() > 150 ) {
                announce = announce.substring(0,150);
            }
            postResponse.setAnnounce(announce);

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

        //Ищем текст в названии
        pagedPosts = postRepository.findAll(QPost.post.title.toLowerCase().contains(query),pageReq);
        for (Post post : pagedPosts){
            PostResponse postResponse = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            //Преобразовать post в postresponse
            postResponse.setId(post.getId());
            postResponse.setIsActive(post.getIsActive());
            postResponse.setModeratorId(post.getModeratorId());
            //Анонс
            String announce = Jsoup.parse(post.getText()).text();
            if (announce.length() > 150 ) {
                announce = announce.substring(0,150);
            }
            postResponse.setAnnounce(announce);
            postResponse.setTimestamp(post.getTime().getTime()/1000);

            postResponse.setTitle(post.getTitle());
            postResponse.setViewCount(post.getViewCount());
            userPostResponse.setName(post.getUser().getName());
            userPostResponse.setId(post.getUser().getId());
            postResponse.setUser(userPostResponse);

            //Если искомый текст есть и в названии и в самом тексте, то не добавляем его
            if (post.getIsActive()==1 && !postArrayList.contains(postResponse)){
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
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"votesSet");
            // !       best - сортировать по убыванию количества лайков
        }
        else if (mode.equals("popular")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"commentsSet");
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
            //Анонс
            String announce = Jsoup.parse(post.getText()).text();
            if (announce.length() > 150 ) {
                announce = announce.substring(0,150);
            }
            postResponse.setAnnounce(announce);
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

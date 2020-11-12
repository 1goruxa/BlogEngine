package main.service;

import main.Repo.*;
import main.api.response.*;
import main.model.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private Tag2PostRepository tag2PostRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private UserRepository userRepository;
    final int ANNOUNCE_LENGTH = 150;
    final String DISLIKE_VALUE = "-1";
    final String LIKE_VALUE = "1";



    //Возврат поста по ID
    public ResponseEntity<Post> getPostById(int id) {
        Post postById = postRepository.findById(id).orElseThrow(ThereIsNoSuchPostException::new);;

        boolean influencedCounter = false;

        //! Увеличивем view_count в этом блоке (пока без правил)
        influencedCounter = true; //Флаг того что мы можем повлиять на изменение счетчика
        postById.setViewCount(postById.getViewCount()+1);
        postRepository.save(postById);
        //----
        PostByIdResponse postByIdResponse = new PostByIdResponse();
        UserPostResponse userPostResponse = new UserPostResponse(postById.getUser().getId(), postById.getUser().getName());

        postByIdResponse.setId(postById.getId());
        postByIdResponse.setTimestamp(postById.getTime().getTime()/1000);

        if (postById.getIsActive()==1){
            postByIdResponse.setActive(true);
        }
        else{
            postByIdResponse.setActive(false);
        }
        postByIdResponse.setUser(userPostResponse);
        postByIdResponse.setTitle(postById.getTitle());
        postByIdResponse.setText(postById.getText());
        postByIdResponse.setViewCount(postById.getViewCount());

        List<Comment> commentsList = commentRepository.findAllByPost(postById);

        ArrayList<CommentPostResponse> commentPostResponseArrayList = new ArrayList<>();
        //преобразовать в CommentPostResponse
        for(Comment c : commentsList){
            UserCommentResponse ucResponse = new UserCommentResponse();
            ucResponse.setId(c.getUser().getId());
            ucResponse.setName(c.getUser().getName());
            ucResponse.setPhoto(c.getUser().getPhoto());
            CommentPostResponse cpResponse = new CommentPostResponse();
            cpResponse.setUser(ucResponse);
            cpResponse.setId(c.getId());
            cpResponse.setText(c.getText());
            cpResponse.setTimestamp(c.getTime().getTime()/1000);
            commentPostResponseArrayList.add(cpResponse);
        }
        postByIdResponse.setComments(commentPostResponseArrayList);

        // Ищем связанные теги
        ArrayList<String> tagArray = new ArrayList<>();
        List<Tag2Post> tag2PostList = postById.getTag2PostList();

        tag2PostList.forEach(t -> {
            tagArray.add(t.getTag().getText());
        });
        postByIdResponse.setTags(tagArray);

        postByIdResponse.setLikeCount(likeDislikeCounter(LIKE_VALUE, postById));
        postByIdResponse.setDislikeCount(likeDislikeCounter(DISLIKE_VALUE, postById));

        //Чтобы наш просмотр не влиял на количество просмотров сразу при открытии.
        if(influencedCounter) {postByIdResponse.setViewCount(postByIdResponse.getViewCount()-1);}

        return new ResponseEntity(postByIdResponse, HttpStatus.OK);
    }

    @ControllerAdvice
    public class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(ThereIsNoSuchPostException.class)
        protected ResponseEntity<ThereIsNoSuchPostException> handleThereIsNoSuchUserException() {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class ThereIsNoSuchPostException extends RuntimeException {
    }

    //Получаем список найденный по запросу постов
    public PostsResponse searchAndGetPosts(int offset, int limit, String query){
        PostsResponse postsResponse = new PostsResponse();
        //Вывод поиска по свежим постам
        PageRequest pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");;
        //У запроса обрежем пробелы
        query.trim();
        query = query.toLowerCase();
        //Ищем поиском в тексте
        List <Post> filteredPosts = postRepository.getPostsByQuery(query);
        Page<Post> pagedPosts = new PageImpl<>(filteredPosts);
        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        int localInActiveCounter = 0;
        for (Post post : pagedPosts){
            postArrayList.add(mapPost2PostResponse(post));
        }

        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(localInActiveCounter);
        return postsResponse;
    }

    //Вывод постов по дате в календаре
    public PostsResponse searchPostsByDate(int offset, int limit, String date){
        PostsResponse postsResponse = new PostsResponse();
        PageRequest pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        List <Post> filteredPosts = postRepository.getPostsByDate(date);
        Page<Post> pagedPosts = new PageImpl<>(filteredPosts);
        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            postArrayList.add(mapPost2PostResponse(post));
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(filteredPosts.size());
        return postsResponse;
    }

    //Вывод постов по тегу
    public PostsResponse searchPostsByTag(int offset, int limit, String tag){
        PostsResponse postsResponse = new PostsResponse();
        PageRequest pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        List <Post> filteredPosts = postRepository.getPostsByTag(tag);
        Page<Post> pagedPosts = new PageImpl<>(filteredPosts);
        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            postArrayList.add(mapPost2PostResponse(post));
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(filteredPosts.size());

        return postsResponse;
    }

    //Получаем список постов в соответсвии с режим отображения mode
    public PostsResponse getPosts(int offset, int limit, String mode){
        PostsResponse postsResponse = new PostsResponse();
        List<Post> filteredPosts;
        Page<Post> pagedPosts = null;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        int countAvilablePosts = 0;

        switch (mode) {
            case "recent": {
                filteredPosts = postRepository.getRecentPosts(pageable);
                countAvilablePosts = postRepository.countAllByIsActiveAndModerationStatusAndTimeLessThan(1, "ACCEPTED",new Date());
                break;
            }
            case "early": {
                filteredPosts = postRepository.getEarlyPosts(pageable);
                countAvilablePosts = postRepository.countAllByIsActiveAndModerationStatusAndTimeLessThan(1, "ACCEPTED",new Date());
                break;
            }
            case "best": {
                filteredPosts = postRepository.getBestPosts(pageable);
                countAvilablePosts = postRepository.counter4Best();
                break;
            }
            case "popular": {
                filteredPosts = postRepository.getDiscussedPosts(pageable);
                countAvilablePosts = postRepository.counter4Discussd();
                break;
            }
            default: {
                filteredPosts = Collections.emptyList();
            }
        }
        pagedPosts = new PageImpl<>(filteredPosts);

        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            postArrayList.add(mapPost2PostResponse(post));
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(countAvilablePosts);
        return postsResponse;
    }


    public PostsResponse myPosts(int offset, int limit, String status, Principal principal){
        PostsResponse postsResponse = new PostsResponse();
        List<Post> filteredPosts;
        Page<Post> pagedPosts = null;
        Pageable pageable = PageRequest.of(offset / limit, limit);
        int countAvilablePosts = 0;
        Optional<User> optionalUser = userRepository.findOneByEmail(principal.getName());
        User currentUser = optionalUser.get();


        //status - статус модерации:
        //inactive - скрытые, ещё не опубликованы (is_active = 0)
        //pending - активные, ожидают утверждения модератором (is_active = 1, moderation_status = NEW)
        //declined - отклонённые по итогам модерации (is_active = 1, moderation_status = DECLINED)
        //published - опубликованные по итогам модерации (is_active = 1, moderation_status = ACCEPTED)

        switch (status){
            case "inactive":{
                filteredPosts = postRepository.getMyInactivePosts(pageable,currentUser.getId());
                countAvilablePosts = postRepository.countMyInactivePosts(currentUser.getId());
                break;
            }
            case "pending":{
                filteredPosts = postRepository.getMyPendingPosts(pageable,currentUser.getId());
                countAvilablePosts = postRepository.countMyPendingPosts(currentUser.getId());
                break;
            }
            case "declined":{
                filteredPosts = postRepository.getMyDeclinedPosts(pageable,currentUser.getId());
                countAvilablePosts = postRepository.countMyDeclinedPosts(currentUser.getId());
                break;
            }
            case "published":{
                filteredPosts = postRepository.getMyPublishedPosts(pageable,currentUser.getId());
                countAvilablePosts = postRepository.countMyPublishedPosts(currentUser.getId());
                break;
            }
            default: {
                filteredPosts = Collections.emptyList();
            }
        }

        pagedPosts = new PageImpl<>(filteredPosts);

        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            postArrayList.add(mapPost2PostResponse(post));
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(countAvilablePosts);

        return postsResponse;
    }

    //Маппинг Post -> PostResponse
    public PostResponse mapPost2PostResponse(Post post){

        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        String textWithoutTags = Jsoup.parse(post.getText()).text();
        postResponse.setAnnounce(textWithoutTags.substring(0, Math.min(ANNOUNCE_LENGTH, textWithoutTags.length())));
        postResponse.setTimestamp(post.getTime().getTime() / 1000);
        postResponse.setTitle(post.getTitle());
        postResponse.setViewCount(post.getViewCount());
        User author = post.getUser();
        postResponse.setUser(new UserPostResponse(author.getId(), author.getName())); // для UserPostResponse создан отдельный конструктор
        postResponse.setLikeCount(likeDislikeCounter(LIKE_VALUE, post));
        postResponse.setDislikeCount(likeDislikeCounter(DISLIKE_VALUE, post));
        postResponse.setCommentCount(post.getCommentsList().size());

       return postResponse;
    }

    public Integer likeDislikeCounter (String type, Post post){

        int counter = (int) post
                .getVotesSet()
                .stream()
                .filter(v -> v.getValue().equals(LIKE_VALUE))
                .count();;

        return counter;
    }

}

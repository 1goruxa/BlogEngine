package main.service;

import main.Repo.*;
import main.api.response.*;
import main.model.*;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    int announceLimit = 150;

    //Возврат поста по ID
    public ResponseEntity<Post> getPostById(int id){
        Optional<Post> optionalPosts = postRepository.findById(id);
        boolean influencedCounter = false;

        //! Увеличивем view_count в этом блоке (пока без правил)
        influencedCounter = true; //Флаг того что мы можем повлиять на изменение счетчика
        Post post = optionalPosts.get();
        post.setViewCount(post.getViewCount()+1);
        postRepository.save(post);
        //----

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


            // Ищем связанные теги
            ArrayList<String> tagArray = new ArrayList<>();
            Iterable<Tag2Post> tag2PostsIterable = tag2PostRepository.findAll(QTag2Post.tag2Post.post.id.eq(optionalPosts.get().getId()));
            ArrayList<Tag2Post> tag2PostArrayList = new ArrayList<>();

            for (Tag2Post t : tag2PostsIterable){
                tag2PostArrayList.add(t);
            }
            tag2PostArrayList.forEach(t ->{
                ArrayList<Tag> singleTagArray = new ArrayList<>();
                Iterable<Tag> tagIterable =  tagRepository.findAll(QTag.tag.id.eq(t.getId()));
                for (Tag tag : tagIterable){
                    singleTagArray.add(tag);
                }
                for (Tag tag : singleTagArray){
                    tagArray.add(tag.getText());
                }
            });
            postByIdResponse.setTags(tagArray);


            //Лайки и дизлайки
            Iterable<Vote> voteIterable = voteRepository.findAll(QVote.vote.post.id.eq(optionalPosts.get().getId()));
            ArrayList<Vote> voteArrayList = new ArrayList<>();
            voteIterable.forEach(v ->{
                voteArrayList.add(v);
            });
            AtomicInteger likeCounter = new AtomicInteger();
            AtomicInteger dislikeCounter = new AtomicInteger();
            voteArrayList.forEach(v ->{
                if (v.getValue().equals("1")){
                    likeCounter.getAndIncrement();
                }
                else{
                    dislikeCounter.getAndIncrement();
                }
            });

            postByIdResponse.setLikeCount(likeCounter.get());
            postByIdResponse.setDislikeCount(dislikeCounter.get());

            //Чтобы наш просмотр не влиял на количество просмотров сразу при открытии.
            if(influencedCounter) {postByIdResponse.setViewCount(postByIdResponse.getViewCount()-1);}

            return new ResponseEntity(postByIdResponse, HttpStatus.OK);

        }

        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        List<Post> filteredPosts = new ArrayList<>();
        Page<Post> pagedPosts = null;
        Iterable<Post> allPosts4LengthDetection = null;

        if (mode.equals("recent")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        }
        else if (mode.equals("early")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.ASC,"time");
        }
        else if (mode.equals("best")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"count");
            filteredPosts = postRepository.getBestPosts(pageReq);

        }
        else if (mode.equals("popular")){
            pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"count");
            filteredPosts = postRepository.getDiscussedPosts(pageReq);
        }

        if (mode.equals("recent")||mode.equals("early")){
            pagedPosts = postRepository.findAll(QPost.post.isActive.eq(1).and(QPost.post.time.loe(new Date())).and(QPost.post.moderationStatus.eq("ACCEPTED")),pageReq);

        }
        if (mode.equals("popular")||mode.equals("best")){
            pagedPosts = new PageImpl<>(filteredPosts);
        }

        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            PostResponse postResponse = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            //Преобразовать post в postresponse
            postResponse.setId(post.getId());

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
            //Счетчики лайков, дизлайков и комментов
            Iterable<Comment> commentIterable = commentRepository.findAll(QComment.comment.post.id.eq((int) postResponse.getId()));
            ArrayList<Comment> commentArrayList = new ArrayList<>();
            for (Comment c: commentIterable){
                commentArrayList.add(c);
            }
            postResponse.setCommentCount(commentArrayList.size());

            Iterable<Vote> voteIterable = voteRepository.findAll(QVote.vote.post.id.eq((int) postResponse.getId()));
            int likeCounter = 0;
            int dislikeCounter = 0;
            for (Vote v : voteIterable){
                if (v.getValue().equals("1")){
                    likeCounter++;
                }
                else{
                    dislikeCounter++;
                }
            }
            postResponse.setLikeCount(likeCounter);
            postResponse.setDislikeCount(dislikeCounter);

            postArrayList.add(postResponse);
        }
        postsResponse.setPosts(postArrayList);

        //Далее только для early и recent mode
        allPosts4LengthDetection = postRepository.findAll(QPost.post.isActive.eq(1).and(QPost.post.time.loe(new Date())).and(QPost.post.moderationStatus.eq("ACCEPTED")));
        int allPosts4LengthDetectionCounter = 0;
        for(Post p : allPosts4LengthDetection){
            allPosts4LengthDetectionCounter++;
        }

        if (mode.equals("best") || mode.equals("popular")){
            postsResponse.setCount(filteredPosts.size());
        }
        else if (mode.equals("early") || mode.equals("recent")){
            postsResponse.setCount(allPosts4LengthDetectionCounter);
        }

        return postsResponse;
    }

    //!вывод постов по тегу общий вывод с recent/early/popular/best вынести в отдельный метод и рассмотреть отказ от querydsl
    //Не работает пагинация byTag
    public PostsResponse searchPostsByTag(int offset, int limit, String tag){
        System.out.println(tag);
        PostsResponse postsResponse = new PostsResponse();
        PageRequest pageReq = PageRequest.of(offset/limit,limit,Sort.Direction.DESC,"time");
        List <Post> filteredPosts = postRepository.getPostsByTag(tag, pageReq);
        Page<Post> pagedPosts = new PageImpl<>(filteredPosts);
        ArrayList<PostResponse> postArrayList = new ArrayList<>();
        for (Post post : pagedPosts){
            PostResponse postResponse = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            //Преобразовать post в postresponse
            postResponse.setId(post.getId());
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
            //Счетчики лайков, дизлайков и комментов
            Iterable<Comment> commentIterable = commentRepository.findAll(QComment.comment.post.id.eq((int) postResponse.getId()));
            ArrayList<Comment> commentArrayList = new ArrayList<>();
            for (Comment c: commentIterable){
                commentArrayList.add(c);
            }
            postResponse.setCommentCount(commentArrayList.size());
            Iterable<Vote> voteIterable = voteRepository.findAll(QVote.vote.post.id.eq((int) postResponse.getId()));
            int likeCounter = 0;
            int dislikeCounter = 0;
            for (Vote v : voteIterable){
                if (v.getValue().equals("1")){
                    likeCounter++;
                }
                else{
                    dislikeCounter++;
                }
            }
            postResponse.setLikeCount(likeCounter);
            postResponse.setDislikeCount(dislikeCounter);
            postArrayList.add(postResponse);
        }
        postsResponse.setPosts(postArrayList);
        postsResponse.setCount(filteredPosts.size());

        return postsResponse;
    }
}

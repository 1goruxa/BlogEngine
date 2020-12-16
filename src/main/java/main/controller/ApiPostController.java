package main.controller;

import main.repo.PostRepository;
import main.api.request.LikeDislikeRequest;
import main.api.request.ModerationRequest;
import main.api.request.SavePostRequest;
import main.api.response.LikeDislikeResponse;
import main.api.response.ModerationResponse;
import main.api.response.NewPostResponse;
import main.api.response.PostsResponse;
import main.repo.UserRepository;
import main.service.LikeDislikeService;
import main.service.PostService;
import main.service.SaveEditPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
public class ApiPostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postsRepository;

    private final PostService postService;
    private final SaveEditPostService saveEditPostService;
    private final LikeDislikeService likeDislikeService;

    public ApiPostController(UserRepository userRepository, PostRepository postsRepository, PostService postService, SaveEditPostService saveEditPostService, LikeDislikeService likeDislikeService) {
        this.userRepository = userRepository;
        this.postsRepository = postsRepository;
        this.postService = postService;
        this.saveEditPostService = saveEditPostService;
        this.likeDislikeService = likeDislikeService;
    }


    @GetMapping("/api/post")
    private PostsResponse postResponse(@RequestParam(required = false, defaultValue = "0") int offset,
                                       @RequestParam(required = false, defaultValue = "0") int limit,
                                       @RequestParam(required = false, defaultValue = "recent") String mode) {

        return postService.getPosts(offset, limit, mode);
    }

    @PostMapping("/api/post")
    private NewPostResponse savePost(@RequestBody SavePostRequest postRequest, Principal principal){

        return saveEditPostService.savePost(postRequest, principal);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getSinglePostById(@PathVariable int id, Principal principal) {
        return postService.getPostById(id, principal);

    }

    @PutMapping("/api/post/{id}")
    public NewPostResponse editPostById(@RequestBody SavePostRequest postRequest, @PathVariable int id, Principal principal){
        return saveEditPostService.editPost(postRequest, id, principal);
    }

    @GetMapping("/api/post/search")
    private PostsResponse postSearchResponse(@RequestParam(required = false, defaultValue = "0") int offset,
                                             @RequestParam(required = false, defaultValue = "0") int limit,
                                             @RequestParam(required = false, defaultValue = "") String query){
        return postService.searchAndGetPosts(offset, limit, query);
    }

    @GetMapping("api/post/byTag")
    public PostsResponse postsByTag(@RequestParam(required = false, defaultValue = "0") int offset,
                                    @RequestParam(required = false, defaultValue = "0") int limit,
                                    @RequestParam(required = false, defaultValue = "") String tag){
        return postService.searchPostsByTag(offset, limit, tag);
    }

    @GetMapping("api/post/byDate")
    public PostsResponse postsByDate(@RequestParam(required = false, defaultValue = "0") int offset,
                                     @RequestParam(required = false, defaultValue = "0") int limit,
                                     @RequestParam(required = false, defaultValue = "") String date){
        return postService.searchPostsByDate(offset, limit, date);
    }

    @GetMapping("api/post/my")
    public PostsResponse myPosts(@RequestParam(required = false, defaultValue = "0") int offset,
                                 @RequestParam(required = false, defaultValue = "0") int limit,
                                 @RequestParam(required = false, defaultValue = "recent") String status,
                                 Principal principal){

        return postService.myPosts(offset, limit, status, principal);
    }

    @PostMapping("api/post/like")
    public LikeDislikeResponse likePost(@RequestBody LikeDislikeRequest request, Principal principal){
        return likeDislikeService.likeDislikePost("1", request, principal);
    }

    @PostMapping("api/post/dislike")
    public LikeDislikeResponse dislikePost(@RequestBody LikeDislikeRequest request, Principal principal){
        return likeDislikeService.likeDislikePost("-1", request, principal);
    }

    @GetMapping("/api/post/moderation")
    public PostsResponse postModeration(@RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "0") int limit,
                                        @RequestParam(required = false, defaultValue = "recent") String status,
                                        Principal principal){
        return postService.postModeration(offset,limit,status,principal);
    }

    @PostMapping("/api/moderation")
    public ModerationResponse moderateDecision(@RequestBody ModerationRequest moderationRequest, Principal principal){

        return postService.moderateDecision(moderationRequest, principal);
    }



}

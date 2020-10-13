package main;

import main.Repo.PostRepository;
import main.api.response.PostsResponse;
import main.Repo.UserRepository;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postsRepository;

    private final PostService postService;

    public ApiPostController(PostService postService) { this.postService = postService; }

    @GetMapping("/api/post")
    private PostsResponse postResponse(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {

        return postService.showPosts(offset, limit, mode);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getSinglePostById(@PathVariable int id) {
        return postService.getPostById(id);

    }

    @GetMapping("/api/post/search")
    private PostsResponse postSearchResponse(@RequestParam int offset, @RequestParam int limit, @RequestParam String query){
        return postService.searchAndShowPosts(offset, limit, query);
    }

}

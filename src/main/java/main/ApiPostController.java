package main;

import main.Repo.PostRepository;
import main.api.response.PostsResponse;
import main.Repo.UserRepository;
import main.api.response.TagsResponse;
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
    private PostsResponse postResponse(@RequestParam(required = false, defaultValue = "0") int offset,
                                       @RequestParam(required = false, defaultValue = "0") int limit,
                                       @RequestParam(required = false, defaultValue = "recent") String mode) {

        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity getSinglePostById(@PathVariable int id) {
        return postService.getPostById(id);

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
}

package main;

import main.api.response.PostResponse;
import main.model.PostsRepository;
import main.model.UsersRepository;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    @Autowired
    private UsersRepository usersRepository;

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/api/post")
    private PostResponse postResponse(@RequestParam int offset, @RequestParam int limit, @RequestParam String mode){

        return postService.showPosts(offset, limit, mode);
    }
}

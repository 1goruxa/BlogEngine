package main.service;

import main.Repo.PostRepository;
import main.Repo.UserRepository;
import main.Repo.VoteRepository;
import main.api.request.LikeDislikeRequest;
import main.api.response.LikeDislikeResponse;
import main.model.Post;
import main.model.User;
import main.model.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
public class LikeDislikeService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;



    public LikeDislikeResponse likePost(LikeDislikeRequest request, Principal principal){
        LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        likeDislikeResponse.setResult(false);

        if (optionalUser != null) {
         User user = optionalUser.get();
         Vote vote = voteRepository.findOneByIdAndType(user.getId(), request.getPostId(), "1");

         if (vote == null) {
             vote = voteRepository.findOneByIdAndType(user.getId(), request.getPostId(), "-1");
             if (vote == null) {
                 vote = new Vote();
                 Optional<Post> optionalPost = postRepository.findById(request.getPostId());
                 Post post = optionalPost.get();
                 vote.setPost(post);
                 vote.setUser(user);
                 vote.setValue("1");
                 vote.setTime(new Date());
                 voteRepository.save(vote);
                 likeDislikeResponse.setResult(true);
             }
             else {
                 voteRepository.delete(vote);
                 vote.setValue("1");
                 vote.setTime(new Date());
                 voteRepository.save(vote);
                 likeDislikeResponse.setResult(true);
             }

         }
     }
        return likeDislikeResponse;
    }

    public LikeDislikeResponse dislikePost(LikeDislikeRequest request, Principal principal){
        LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        likeDislikeResponse.setResult(false);

        if (optionalUser != null) {
            User user = optionalUser.get();
            Vote vote = voteRepository.findOneByIdAndType(user.getId(), request.getPostId(), "-1");

            if (vote == null) {
                vote = voteRepository.findOneByIdAndType(user.getId(), request.getPostId(), "1");
                if (vote == null) {
                    vote = new Vote();
                    Optional<Post> optionalPost = postRepository.findById(request.getPostId());
                    Post post = optionalPost.get();
                    vote.setPost(post);
                    vote.setUser(user);
                    vote.setValue("-1");
                    vote.setTime(new Date());
                    voteRepository.save(vote);
                    likeDislikeResponse.setResult(true);
                }
                else {
                    voteRepository.delete(vote);
                    vote.setValue("-1");
                    vote.setTime(new Date());
                    voteRepository.save(vote);
                    likeDislikeResponse.setResult(true);
                }

            }
        }
        return likeDislikeResponse;

      }


}

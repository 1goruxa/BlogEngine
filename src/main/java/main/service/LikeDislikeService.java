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


    public LikeDislikeResponse likeDislikePost(String type, LikeDislikeRequest request, Principal principal){
        LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
        User currentUser;
        Optional<User> optionalUser = null;
        likeDislikeResponse.setResult(false);
        if (principal != null) {
            likeDislikeResponse.setResult(true);
            optionalUser = userRepository.findOneByEmail(principal.getName());
        }
            if (optionalUser != null) {
                currentUser = optionalUser.get();
                boolean alreadyVoted = false;
                boolean altVoted = false;
                Vote altVoteExist = new Vote();
                //Проверяем голосовал ли пользователь уже
                Optional<Vote> duplicateVote = voteRepository.findOneByIdAndType(currentUser.getId(), request.getPostId(), type);
                if(duplicateVote.isPresent()){
                    alreadyVoted = true;
                }
                else{
                    //Было ли альтернативное голосование (дизлайк на лайк и наоборот)
                    String altType;
                    if (type.equals("1")){
                        altType = "-1";
                    }
                    else{
                        altType = "1";
                    }
                    Optional<Vote> altVote = voteRepository.findOneByIdAndType(currentUser.getId(), request.getPostId(), altType);
                    if (altVote.isPresent()){
                        altVoted = true;
                        altVoteExist = altVote.get();
                    }
                }

                //Действия в зависимости от предыдущих проверок
                if(alreadyVoted){
                    likeDislikeResponse.setResult(false);
                }
                else {
                    Vote vote = new Vote();
                    Optional<Post> optionalPost = postRepository.findById(request.getPostId());
                    Post post = optionalPost.get();
                    vote.setPost(post);
                    vote.setUser(currentUser);
                    vote.setValue(type);
                    vote.setTime(new Date());

                    if (altVoted) {
                        vote.setId(altVoteExist.getId());
                        voteRepository.delete(altVoteExist);
                    }
                    voteRepository.save(vote);
                }
            }
        return likeDislikeResponse;
    }
}

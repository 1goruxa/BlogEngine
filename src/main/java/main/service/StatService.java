package main.service;

import main.Repo.*;
import main.api.response.StatResponse;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;


@Service
public class StatService {
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

    //Подтягиваем запросы соответствующих репо
    public StatResponse getAllStat(){
        StatResponse statResponse = new StatResponse();

        statResponse.setPostsCount(postRepository.getPostsCount4AllStat());
        statResponse.setLikesCount(voteRepository.getVotesCount4AllStat(1));
        statResponse.setDislikesCount(voteRepository.getVotesCount4AllStat(-1));
        statResponse.setViewsCount(postRepository.getViewsCounter4AllStat());
        statResponse.setFirstPublication(postRepository.getFirstPublication4AllStat().getTime()/1000);

        return statResponse;

    }

    public StatResponse getMyStats(Principal principal){
        StatResponse statResponse = new StatResponse();

        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            statResponse.setPostsCount(postRepository.getPostsCount4MyStat(currentUser.getId()));
            statResponse.setLikesCount(voteRepository.getVotesCount4MyStat(1,currentUser.getId()));
            statResponse.setDislikesCount(voteRepository.getVotesCount4MyStat(-1,currentUser.getId()));
            statResponse.setViewsCount(postRepository.getViewsCounter4MyStat(currentUser.getId()));
            statResponse.setFirstPublication(postRepository.getFirstPublication4MylStat(currentUser.getId()).getTime() / 1000);
        }

        return statResponse;
    }
}

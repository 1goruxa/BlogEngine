package main.service;

import main.Repo.*;
import main.api.response.StatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
}

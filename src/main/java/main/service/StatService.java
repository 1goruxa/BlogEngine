package main.service;

import main.Repo.*;
import main.api.response.StatResponse;
import main.model.GlobalSettings;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private SettingsRepository settingsRepository;

    //Подтягиваем запросы соответствующих репо
    public ResponseEntity getAllStat(Principal principal){
        StatResponse statResponse = new StatResponse();
        HttpStatus httpStatus = HttpStatus.OK;
        User currentUser;
        boolean moderAuthorized = false;
        //Проверим есть ли у авторизованного пользователя права модератора
        if(principal != null) {
            Optional<User> optionalUser = userRepository.findOneByEmail(principal.getName());
            if (optionalUser.isPresent()) {
                currentUser = optionalUser.get();
                if (currentUser.getIsModerator() == 1) {
                    moderAuthorized = true;
                }
            }
        }
        //Проверим глобальную настройку права просмотра статистики
        GlobalSettings globalSettings = settingsRepository.findOneByName("STATISTICS_IS_PUBLIC");
        String statPublicSettngs = globalSettings.getValue();
        boolean showStats = false;

        if(statPublicSettngs.equals("0")){
            if(moderAuthorized){
                showStats = true;
            }
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        else{
            showStats = true;
        }

        if (showStats) {
            statResponse.setPostsCount(postRepository.getPostsCount4AllStat());
            statResponse.setLikesCount(voteRepository.getVotesCount4AllStat(1));
            statResponse.setDislikesCount(voteRepository.getVotesCount4AllStat(-1));
            statResponse.setViewsCount(postRepository.getViewsCounter4AllStat());
            statResponse.setFirstPublication(postRepository.getFirstPublication4AllStat().getTime() / 1000);
        }
        return new ResponseEntity(statResponse, httpStatus);
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

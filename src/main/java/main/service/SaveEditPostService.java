package main.service;

import main.repo.*;
import main.api.request.SavePostRequest;
import main.api.response.ErrorsOnPostResponse;
import main.api.response.NewPostResponse;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
public class SaveEditPostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    //Редактирование поста
    public NewPostResponse editPost(SavePostRequest savePostRequest, int id,  Principal principal){

    NewPostResponse newPostResponse = new NewPostResponse();
    ErrorsOnPostResponse errorsOnPostResponse = new ErrorsOnPostResponse();

    Optional<User> optionalUser = null;
    if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
    if (optionalUser.isPresent()) {

        User currentUser = optionalUser.get();
        Optional<Post> optionalPost = postRepository.findById(id);
        Post post = optionalPost.get();
        if (savePostRequest.getTitle().length() > 3 && savePostRequest.getText().length() > 50){

            int postId = post.getId();
            post.setText(savePostRequest.getText());
            post.setIsActive(savePostRequest.getActive());
            post.setId(postId);
            post.setTitle(savePostRequest.getTitle());
            if (currentUser.getIsModerator() != 1){
                GlobalSettings globalSettings = settingsRepository.findOneByName("POST_PREMODERATION");
                String postPremoderationSettngs = globalSettings.getValue();
                //Если включена модерация постов и автор не модер, то пост улетает на модерацию
                //Иначе он останется в своем текущем статусе
                if(postPremoderationSettngs.equals("1")) {
                    post.setModerationStatus("NEW");
                }
            }
            if (savePostRequest.getTimestamp() < System.currentTimeMillis()/1000) {
                post.setTime(new Date());
            }
            else{
                post.setTime(new Date(savePostRequest.getTimestamp()*1000));
            }

            postRepository.save(post);
            newPostResponse.setResult(true);

            savePostRequest.getTags().forEach(t ->{
                Tag tag = tagRepository.findOneByText(t);
                if (tag == null){
                    tag = new Tag();
                    tag.setText(t);
                    tagRepository.save(tag);
                }
                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setPost(post);
                tag2Post.setTag(tag);
                Optional<Tag2Post> optionalTag2Post = tag2PostRepository.findEqual(post.getId(), tag.getId());
                if(!optionalTag2Post.isPresent()){
                    tag2PostRepository.save(tag2Post);
                }
            });
        }
        else{
            if (savePostRequest.getTitle().length() < 3){
                errorsOnPostResponse.setTitle("Заголовок не установлен");
                newPostResponse.setErrors(errorsOnPostResponse);
            }
            else if(savePostRequest.getText().length() < 50){
                errorsOnPostResponse.setText("Текст публикации слишком короткий");
                newPostResponse.setErrors(errorsOnPostResponse);
            }
        }
    }
    return newPostResponse;
}


    public NewPostResponse savePost(SavePostRequest postRequest, Principal principal){
        // POST_PREMODERATION - если включен этот режим, то все новые посты пользователей с moderation = false
        // обязательно должны попадать на модерацию, у постов при создании должен быть установлен moderation_status = NEW.


        NewPostResponse newPostResponse = new NewPostResponse();
        ErrorsOnPostResponse errorsOnPostResponse = new ErrorsOnPostResponse();
        GlobalSettings globalSettings = settingsRepository.findOneByName("POST_PREMODERATION");
        String postPremoderationSettngs = globalSettings.getValue();

        if (principal != null) {
            if (postRequest.getTitle().length() < 3){
                newPostResponse.setResult(false);
                errorsOnPostResponse.setTitle("Заголовок не установлен");
                newPostResponse.setErrors(errorsOnPostResponse);
            }
            if(postRequest.getText().length() < 50){
                newPostResponse.setResult(false);
                errorsOnPostResponse.setText("Текст публикации слишком короткий");

                newPostResponse.setErrors(errorsOnPostResponse);
            }
            if (postRequest.getTitle().length() >=3 && postRequest.getText().length() >= 50){
                newPostResponse.setResult(true);
                newPostResponse.setErrors(new ErrorsOnPostResponse());
                Post post = new Post();
                post.setTitle(postRequest.getTitle());
                post.setText(postRequest.getText());
                post.setIsActive(postRequest.getActive());
                Optional<User> optionaluser = userRepository.findOneByEmail(principal.getName());
                User user = optionaluser.get();
                post.setUser(user);
                post.setModerationStatus("NEW");

                // POST_PREMODERATION - если включен этот режим, у пользователя moderation = true
                // у постов при создании должен быть установлен moderation_status = ACCEPTED.
                if (postPremoderationSettngs.equals("1") && user.getIsModerator() == 1){
                    post.setModerationStatus("ACCEPTED");
                }
                // Eсли значения POST_PREMODERATION = false (режим премодерации выключен),
                // то все новые посты должны сразу публиковаться (если у них установлен параметр active = 1),
                // у постов при создании должен быть установлен moderation_status = ACCEPTED.
                if (postPremoderationSettngs.equals("0") && post.getIsActive() == 1){
                    post.setModerationStatus("ACCEPTED");
                }

                if (postRequest.getTimestamp() < System.currentTimeMillis()/1000) {
                    post.setTime(new Date());
                }
                else{
                    post.setTime(new Date(postRequest.getTimestamp()*1000));
                }
                postRepository.save(post);
                postRequest.getTags().forEach(t ->{
                    Tag tag = tagRepository.findOneByText(t);

                    //На случай если фронт будет переделан с возможностью добавки новых тэгов
                    if (tag == null){
                        tag = new Tag();
                        tag.setText(t);
                        tagRepository.save(tag);
                    }

                    Tag2Post tag2Post = new Tag2Post();
                    tag2Post.setPost(post);
                    tag2Post.setTag(tag);
                    tag2PostRepository.save(tag2Post);
                });
            }

        }
        else {
            newPostResponse.setResult(false);
        }

        return newPostResponse;
    }


}

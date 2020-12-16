package main.service;

import main.repo.CommentRepository;
import main.repo.PostRepository;
import main.repo.UserRepository;
import main.api.request.AddCommentRequest;
import main.api.response.AddCommentResponse;
import main.api.response.ErrorsOnAddingComment;
import main.model.Comment;
import main.model.Post;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public AddCommentResponse addComment(AddCommentRequest addCommentRequest, Principal principal){
        AddCommentResponse addCommentResponse = new AddCommentResponse();

        Optional<User> optionalUser = null;
        addCommentResponse.setResult(false);

        if(principal != null) {
            optionalUser = userRepository.findOneByEmail(principal.getName());

            if (optionalUser.isPresent()) {
                User currentUser = optionalUser.get();
                Comment comment = new Comment();

                Optional<Post> optionalPost = postRepository.findById(addCommentRequest.getPostId());
                Post post = optionalPost.get();
                if(addCommentRequest.getText().length() > 3) {
                    comment.setParentId(addCommentRequest.getParentId());
                    comment.setPost(post);
                    comment.setText(addCommentRequest.getText());
                    comment.setTime(new Date());
                    comment.setUser(currentUser);
                    commentRepository.save(comment);
                    addCommentResponse.setResult(null);
                    addCommentResponse.setId(comment.getId());
                }
                else{
                    ErrorsOnAddingComment errorsOnAddingComment = new ErrorsOnAddingComment();
                    errorsOnAddingComment.setText("Текст комментария не задан или слишком короткий");
                    addCommentResponse.setErrors(errorsOnAddingComment);
                    addCommentResponse.setId(null);
                }
            }
        }
        else {
            try {
                throw new NullPointerException("Пользователь не авторизован");
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }

        return addCommentResponse;
    }
}

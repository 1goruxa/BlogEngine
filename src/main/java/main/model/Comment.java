package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "post_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name="parent_id")
    private int parentId;

    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name="post_id")
    private Post post;


    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name="user_id")
    private User user;

    private String text;
    private Date time;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

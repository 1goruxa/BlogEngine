package main.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name="is_active")
    private int isActive;
    @Column(name="moderator_id")
    private int moderatorId;

    @Column(name="moderation_status")
    private String moderationStatus;

    private String text;
    private Date time;
    private String title;


    @ManyToOne(targetEntity=User.class,optional=false)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @Column(name="view_count")
    private int viewCount;

    @OneToMany(targetEntity = Comment.class, mappedBy = "post", fetch = FetchType.EAGER)
    private List<Comment> commentsList;

    @OneToMany(targetEntity = Vote.class, mappedBy = "post", fetch = FetchType.EAGER)
    private Set<Vote> votesSet;

    @OneToMany(targetEntity = Tag2Post.class, mappedBy = "post")
    private List<Tag2Post> tag2PostList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(int moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    public Set<Vote> getVotesSet() {
        return votesSet;
    }

    public void setVotesSet(Set<Vote> votesSet) {
        this.votesSet = votesSet;
    }

    public List<Tag2Post> getTag2PostList() {
        return tag2PostList;
    }

    public void setTag2PostList(List<Tag2Post> tag2PostList) {
        this.tag2PostList = tag2PostList;
    }
}


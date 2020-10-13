package main.model;

import javax.persistence.*;
import java.util.Date;
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
    private String text;
    private Date time;
    private String title;


    @ManyToOne(targetEntity=User.class,optional=false)
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User user;

    @Column(name="view_count")
    private int viewCount;

    @OneToMany(targetEntity = Comment.class, mappedBy = "post", fetch = FetchType.EAGER)
    private Set<Comment> commentsSet;

    @OneToMany(targetEntity = Vote.class, mappedBy = "post", fetch = FetchType.EAGER)
    private Set<Vote> votesSet;

    @OneToMany(targetEntity = Tag2Post.class, mappedBy = "post")
    private Set<Tag2Post> tag2PostSet;

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
}


//id int AI PK
//        is_active int
//        moderator_id int
//        text varchar(255)
//        time datetime(6)
//        title varchar(255)
//        user_id int
//        view_count int


//"id": 345,
//        "timestamp": 1592338706,
//        "user":
//        {
//        "id": 88,
//        "name": "Дмитрий Петров"
//        },
//        "title": "Заголовок поста",
//        "announce": "Текст анонса поста без HTML-тэгов",
//        "likeCount": 36,
//        "dislikeCount": 3,
//        "commentCount": 15,
//        "viewCount": 55
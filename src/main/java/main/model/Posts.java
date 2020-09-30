package main.model;

import org.dom4j.tree.AbstractEntity;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Posts extends AbstractEntity {
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

    @Column(name="user_id")
    private int user;

    @Column(name="view_count")
    private int viewCount;

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

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
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
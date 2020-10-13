package main.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String code;
    private String email;
    private String password;
    private String photo;

    @Column(name="reg_time")
    private Date regTime;

    @Column(name="is_moderator")
    private int isModerator;
    private String name;

    @OneToMany(mappedBy = "user", fetch=FetchType.EAGER)
    private Set<Post> postSet;

    @OneToMany(targetEntity = Comment.class, mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Comment> commentsSet;

    @OneToMany(targetEntity = Vote.class, mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Vote> votesSet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public int getIsModerator() {
        return isModerator;
    }

    public void setIsModerator(int isModerator) {
        this.isModerator = isModerator;
    }
}

    //id int AI PK
    //        code varchar(255)
    //        email varchar(255)
    //        is_moderator int
    //        name varchar(255)
    //        password varchar(255)
    //        photo varchar(255)
    //        reg_time datetime(6)